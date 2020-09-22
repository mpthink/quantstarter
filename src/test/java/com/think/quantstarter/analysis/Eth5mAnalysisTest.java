package com.think.quantstarter.analysis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.think.quantstarter.analysis.utils.CandleUtil;
import com.think.quantstarter.dataCollect.entity.EthCandles1h;
import com.think.quantstarter.dataCollect.entity.EthCandles4h;
import com.think.quantstarter.dataCollect.entity.EthCandles5m;
import com.think.quantstarter.dataCollect.mapper.EthCandles1hMapper;
import com.think.quantstarter.dataCollect.mapper.EthCandles1mMapper;
import com.think.quantstarter.dataCollect.mapper.EthCandles4hMapper;
import com.think.quantstarter.dataCollect.mapper.EthCandles5mMapper;
import com.think.quantstarter.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author mpthink
 * @date 2020/9/17 10:56
 */
@SpringBootTest
public class Eth5mAnalysisTest {
    @Resource
    private EthCandles1mMapper ethCandles1mMapper;
    @Resource
    private EthCandles5mMapper ethCandles5mMapper;
    @Resource
    private EthCandles1hMapper ethCandles1hMapper;
    @Resource
    private EthCandles4hMapper ethCandles4hMapper;

    /**
     * 策略目的，找出一个正向收益的策略
     * 策略1：
     * 1. 顺序获取每一个5m candle和该candle之前的一条记录
     * 2. 判断前一个5m的ema5-ema10和当前ema5-ema10值是否相反
     * 2. 获取当前5m candle所在的1h 和 4h,以当前5m收盘价重新计算当时的ema，否则不准确
     * 3. 判断当前1h 和 4h 的ema5及ema10之间的关系，<以及4h candle当前是红还是绿>
     * 5. 用当前5m的下一分钟candle的开盘价作为买入价
     * 1~5 为买入点，
     * 下面设置止损和止盈
     * 6. 计算过去<20>次，candle5的平均振幅N，止损1：Price1 = buyPrice +/- 2N
     * 7. 获取过去20次，最低价，以最低价或者最好价作为止损价： Price2
     * 8. 根据买多还是卖空，设置price， 买多： price = Max(price1, price2) ，卖空：Min(price1, price2)
     * 止盈设置： （要不要在盈利达到一定程度后，重新设置止损价？？）
     * 9. 循环遍历1分钟和5分钟candle。
     * 1）定时策略： 1个小时后卖出  --- 当前选择定时策略，调整时间
     * 2）止盈策略： 2N或者4N
     * 3）ema策略： ema5穿过ema10
     * 10. 记录一笔记录的情况
     */
    @Test
    public void testStrategy() throws ParseException {
        //获取最早一条记录
        QueryWrapper<EthCandles5m> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("candle_time");
        wrapper.last("limit 1");
        EthCandles5m oldest = ethCandles5mMapper.selectOne(wrapper);
        String start = oldest.getCandleTime();
        String end = start;
        int i = 0;
        while (DateUtils.parseUTCTime(end).before(new Date())) {
            end = DateUtils.addMinutes(start, 5);
            wrapper = new QueryWrapper<>();
            wrapper.ge("candle_time", start);
            wrapper.le("candle_time", end);
            wrapper.orderByAsc("candle_time");
            start = end;
            if (i == 0) {
                i++;
                continue;
            }
            List<EthCandles5m> ethCandles5ms = ethCandles5mMapper.selectList(wrapper);
            if (ethCandles5ms.size() != 2) {
                System.out.println("No enough data for analysis!");
                return;
            }
            EthCandles5m candleOld = ethCandles5ms.get(0);
            EthCandles5m candleNew = ethCandles5ms.get(1);
            Double minuteEma5 = candleNew.getEma5();
            Double minuteEma10 = candleNew.getEma10();
            //判断前一个5m的ema5-ema10和当前ema5-ema10值是否相反
            if (isDifferentLabel(candleOld, candleNew)) {
                //根据当前5m的close，获取当前1h的EMA5 和 EMA10
                String hourEnd = DateUtils.getUTCWithoutMinutes(candleNew.getCandleTime());
                String hourStart = DateUtils.addMinutes(hourEnd, -60);
                EthCandles1h ethCandles1h = ethCandles1hMapper.selectById(hourStart);
                Double current1hEma5 = CandleUtil.getEMA(Arrays.asList(ethCandles1h.getEma5(), candleNew.getClose()), 5);
                Double current1hEma10 = CandleUtil.getEMA(Arrays.asList(ethCandles1h.getEma10(), candleNew.getClose()), 10);
                //获取4小时 EMA5和EMA10
                String hour4End = get4HourStart(candleNew.getCandleTime());
                String hour4Start = DateUtils.addMinutes(hour4End, -240);
                EthCandles4h ethCandles4h = ethCandles4hMapper.selectById(hour4Start);
                Double current4hEma5 = CandleUtil.getEMA(Arrays.asList(ethCandles4h.getEma5(), candleNew.getClose()), 5);
                Double current4hEma10 = CandleUtil.getEMA(Arrays.asList(ethCandles4h.getEma10(), candleNew.getClose()), 10);
                //以下两个条件符合要求，准备下单（后续可以根据策略调整这个地方来调整买入点） 买入价格为当前5分钟下一分钟的开盘价
                if ((minuteEma5 - minuteEma10) > 0 && (current1hEma5 - current1hEma10) > 0 && (current4hEma5 - current4hEma10) > 0) {
                //if ((minuteEma5 - minuteEma10) > 0 && (current1hEma5 - current1hEma10) > 0 && (current4hEma5 - current4hEma10) > 0 && (candleNew.getClose()-ethCandles4h.getOpen())>0) {
                    Double buyLongPrice = getBuyPrice(candleNew.getCandleTime());
                    //计算止损价，取过去5分钟20次的平均值和最低值， 止盈按定时时间来做
                    Double planLossPrice =getLossPrice(candleNew, buyLongPrice, "long");
                    System.out.print(candleNew.getCandleTime() +" " +candleNew.getOpen()+" 适合买多,买入价：" + buyLongPrice +" 止损价："+planLossPrice);
                    //由于1分钟数据有限，不能获取当前1分钟的价格，按照接下来5分钟的走势，按1个小时定时止损来看看是否会止损或者退出
                    String tempStart = DateUtils.addMinutes(candleNew.getCandleTime(), 5);
                    String tempEnd = DateUtils.addMinutes(candleNew.getCandleTime(), 125);
                    wrapper = new QueryWrapper<>();
                    wrapper.ge("candle_time", tempStart);
                    wrapper.le("candle_time", tempEnd);
                    wrapper.orderByAsc("candle_time");
                    List<EthCandles5m> tempList = ethCandles5mMapper.selectList(wrapper);
                    boolean flag = true;
                    for(EthCandles5m candles5m:tempList){
                        if(candles5m.getLow()<=planLossPrice){
                            System.out.println(" 达到止损价： 卖出,亏损：" + (planLossPrice - buyLongPrice) + "," + (planLossPrice - buyLongPrice)/buyLongPrice*1000);
                            flag = false;
                            break;
                        }
                    }
                    if(flag){
                        Double lastClose = tempList.get(tempList.size()-1).getClose();
                        System.out.println(" 按时卖出： " + lastClose + " 盈亏： " + (lastClose - buyLongPrice) +","+ (lastClose-buyLongPrice)/buyLongPrice*1000);
                    }
                }

                if ((minuteEma5 - minuteEma10) < 0 && (current1hEma5 - current1hEma10) < 0 && (current4hEma5 - current4hEma10) < 0) {
                //if ((minuteEma5 - minuteEma10) < 0 && (current1hEma5 - current1hEma10) < 0 && (current4hEma5 - current4hEma10) < 0 && (candleNew.getClose()-ethCandles4h.getOpen())<0) {
                    Double buyShortPrice = getBuyPrice(candleNew.getCandleTime());
                    //计算止损价，取过去5分钟20次的平均值和最低值， 止盈按定时时间来做
                    Double planLossPrice =getLossPrice(candleNew, buyShortPrice, "short");
                    System.out.print(candleNew.getCandleTime() +" " +candleNew.getOpen()+" 适合卖空,买入价：" + buyShortPrice +" 止损价："+planLossPrice);
                    //由于1分钟数据有限，不能获取当前1分钟的价格，按照接下来5分钟的走势，按1个小时定时止损来看看是否会止损或者退出
                    String tempStart = DateUtils.addMinutes(candleNew.getCandleTime(), 5);
                    String tempEnd = DateUtils.addMinutes(candleNew.getCandleTime(), 125);
                    wrapper = new QueryWrapper<>();
                    wrapper.ge("candle_time", tempStart);
                    wrapper.le("candle_time", tempEnd);
                    wrapper.orderByAsc("candle_time");
                    List<EthCandles5m> tempList = ethCandles5mMapper.selectList(wrapper);
                    boolean flag = true;
                    for(EthCandles5m candles5m:tempList){
                        if(candles5m.getHigh()>=planLossPrice){
                            System.out.println(" 达到止损价： 卖出,亏损：" + (buyShortPrice - planLossPrice) + "," + (buyShortPrice - planLossPrice)/buyShortPrice*1000);
                            flag = false;
                            break;
                        }
                    }
                    if(flag){
                        Double lastClose = tempList.get(tempList.size()-1).getClose();
                        System.out.println(" 按时卖出： " + lastClose + " 盈亏： " + (buyShortPrice - lastClose) + "," + (buyShortPrice - lastClose)/buyShortPrice*1000);
                    }
                }
            }
        }
    }

    private Double getLossPrice(EthCandles5m candle, Double buyPrice, String type){
        String end = candle.getCandleTime();
        String start = DateUtils.addMinutes(end, -100);
        QueryWrapper<EthCandles5m> wrapper = new QueryWrapper<>();
        wrapper.ge("candle_time", start);
        wrapper.le("candle_time", end);
        wrapper.orderByAsc("candle_time");
        List<EthCandles5m> ethCandles5ms = ethCandles5mMapper.selectList(wrapper);
        Double highest = ethCandles5ms.stream().mapToDouble(EthCandles5m::getHigh).max().getAsDouble();
        Double lowest = ethCandles5ms.stream().mapToDouble(EthCandles5m::getLow).min().getAsDouble();
        Double highSum = ethCandles5ms.stream().mapToDouble(EthCandles5m::getHigh).sum();
        Double lowSum = ethCandles5ms.stream().mapToDouble(EthCandles5m::getLow).sum();
        Double avgMargin = (highSum-lowSum)/ethCandles5ms.size();
        //计划损失 1.5N的价格
        Double planLoss = 1 * avgMargin;
        if(type.equals("long")){
            return Math.max(buyPrice-planLoss, lowest);
        }else{
            return Math.min(buyPrice + planLoss, highest);
        }
    }

    //获取买入价，一般等于下一个5分钟的开盘价获取下一个一分钟的开盘价
    private Double getBuyPrice(String time){
        String buyTime = DateUtils.addMinutes(time, 1);
//        EthCandles1m ethCandles1m = ethCandles1mMapper.selectById(buyTime);
//        if (ethCandles1m == null){
//            buyTime = DateUtils.addMinutes(time, 5);
//            EthCandles5m ethCandles5m = ethCandles5mMapper.selectById(buyTime);
//            return ethCandles5m.getOpen();
//        }else {
//            return ethCandles1m.getOpen();
//        }
        buyTime = DateUtils.addMinutes(time, 5);
        EthCandles5m ethCandles5m = ethCandles5mMapper.selectById(buyTime);
        return ethCandles5m.getOpen();
    }


    private static String get4HourStart(String candleTime) throws ParseException {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        Date date = DateUtils.parseUTCTime(candleTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int plus4hour = hour / 4 * 4;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time = format.format(DateUtils.parseUTCTime(candleTime));
        Date onlyDate = format.parse(time);
        calendar.setTime(onlyDate);
        calendar.add(Calendar.HOUR, plus4hour);
        Date calendarTime = calendar.getTime();
        return DateUtils.timeToString(calendarTime, 8);
    }

    private static boolean isDifferentLabel(EthCandles5m one, EthCandles5m two) {
        Double oneEmaMargin = one.getEma5() - one.getEma10();
        Double twoEmaMargin = two.getEma5() - two.getEma10();
        if (oneEmaMargin * twoEmaMargin > 0) {
            return false;
        } else {
            return true;
        }
    }

}
