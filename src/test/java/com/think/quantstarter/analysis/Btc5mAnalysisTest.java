package com.think.quantstarter.analysis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.think.quantstarter.analysis.utils.CandleUtil;
import com.think.quantstarter.dataCollect.entity.BtcCandles1h;
import com.think.quantstarter.dataCollect.entity.BtcCandles1m;
import com.think.quantstarter.dataCollect.entity.BtcCandles4h;
import com.think.quantstarter.dataCollect.entity.BtcCandles5m;
import com.think.quantstarter.dataCollect.mapper.BtcCandles1hMapper;
import com.think.quantstarter.dataCollect.mapper.BtcCandles1mMapper;
import com.think.quantstarter.dataCollect.mapper.BtcCandles4hMapper;
import com.think.quantstarter.dataCollect.mapper.BtcCandles5mMapper;
import com.think.quantstarter.utils.DateUtils;
import lombok.SneakyThrows;
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
public class Btc5mAnalysisTest {
    @Resource
    private BtcCandles1mMapper btcCandles1mMapper;
    @Resource
    private BtcCandles5mMapper btcCandles5mMapper;
    @Resource
    private BtcCandles1hMapper btcCandles1hMapper;
    @Resource
    private BtcCandles4hMapper btcCandles4hMapper;

    private static int positiveTimes = 0;
    private static int negativeTimes = 0;

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
        QueryWrapper<BtcCandles5m> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("candle_time");
        wrapper.last("limit 1");
        BtcCandles5m oldest = btcCandles5mMapper.selectOne(wrapper);
        String start = oldest.getCandleTime();
        String lastBuyTime = start;
        long defaultGap = 60;
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
            List<BtcCandles5m> btcCandles5ms = btcCandles5mMapper.selectList(wrapper);
            if (btcCandles5ms.size() != 2) {
                System.out.println("No enough data for analysis!");
                System.out.println("盈利次数:" + positiveTimes + " 亏损次数: " + negativeTimes);
                return;
            }
            BtcCandles5m candleOld = btcCandles5ms.get(0);
            BtcCandles5m candleNew = btcCandles5ms.get(1);
            Double minuteEma5 = candleNew.getEma5();
            Double minuteEma10 = candleNew.getEma10();
            //判断前一个5m的ema5-ema10和当前ema5-ema10值是否相反
            if (isDifferentLabel(candleOld, candleNew)) {
                //根据当前5m的close，获取当前1h的EMA5 和 EMA10
                String hourEnd = DateUtils.getUTCWithoutMinutes(candleNew.getCandleTime());
                String hourStart = DateUtils.addMinutes(hourEnd, -60);
                BtcCandles1h btcCandles1h = btcCandles1hMapper.selectById(hourStart);
                Double current1hEma5 = CandleUtil.getEMA(Arrays.asList(btcCandles1h.getEma5(), candleNew.getClose()), 5);
                Double current1hEma10 = CandleUtil.getEMA(Arrays.asList(btcCandles1h.getEma10(), candleNew.getClose()), 10);
                //获取4小时 EMA5和EMA10
                String hour4End = get4HourStart(candleNew.getCandleTime());
                String hour4Start = DateUtils.addMinutes(hour4End, -240);
                BtcCandles4h btcCandles4h = btcCandles4hMapper.selectById(hour4Start);
                Double current4hEma5 = CandleUtil.getEMA(Arrays.asList(btcCandles4h.getEma5(), candleNew.getClose()), 5);
                Double current4hEma10 = CandleUtil.getEMA(Arrays.asList(btcCandles4h.getEma10(), candleNew.getClose()), 10);
                //以下两个条件符合要求，准备下单（后续可以根据策略调整这个地方来调整买入点） 买入价格为当前5分钟下一分钟的开盘价
                if ((minuteEma5 - minuteEma10) > 0 && (current1hEma5 - current1hEma10) > 0 && (current4hEma5 - current4hEma10) > 0 && hour4Trend(hourEnd, "up", current4hEma5, current4hEma10)) {
                    //if ((minuteEma5 - minuteEma10) > 0 && (current4hEma5 - current4hEma10) > 0) {

                    hour4Trend(hourEnd, "down", current4hEma5, current4hEma10);

                    //判断是否在120分钟内多次买入
                    if (countTimeGapMinutes(candleNew.getCandleTime(), lastBuyTime) < defaultGap) {
                        continue;
                    } else {
                        lastBuyTime = candleNew.getCandleTime();
                    }

                    Double buyLongPrice = getBuyPrice(candleNew.getCandleTime());
                    //计算止损价，取过去5分钟20次的平均值和最低值， 止盈按定时时间来做
                    Double planLossPrice = getLossPrice(candleNew, buyLongPrice, "long");
                    System.out.print(candleNew.getCandleTime() + " " + candleNew.getOpen() + " 适合买多,买入价：" + buyLongPrice + " 止损价：" + planLossPrice);
                    //由于1分钟数据有限，不能获取当前1分钟的价格，按照接下来5分钟的走势，按1个小时定时止损来看看是否会止损或者退出
                    String tempStart = DateUtils.addMinutes(candleNew.getCandleTime(), 5);
                    String tempEnd = DateUtils.addMinutes(candleNew.getCandleTime(), 125);
                    wrapper = new QueryWrapper<>();
                    wrapper.ge("candle_time", tempStart);
                    wrapper.le("candle_time", tempEnd);
                    wrapper.orderByAsc("candle_time");
                    List<BtcCandles5m> tempList = btcCandles5mMapper.selectList(wrapper);
                    boolean flag = true;
                    for (BtcCandles5m candles5m : tempList) {
                        if (candles5m.getLow() <= planLossPrice) {
                            System.out.println("达到止损价： 卖出,亏损：" + (planLossPrice - buyLongPrice) + "," + (planLossPrice - buyLongPrice) / buyLongPrice * 1000);
                            flag = false;
                            countTimes(planLossPrice - buyLongPrice);
                            break;
                        }
                    }
                    if (flag) {
                        Double lastClose = tempList.get(tempList.size() - 1).getClose();
                        System.out.println(" 按时卖出： " + lastClose + " 盈亏： " + (lastClose - buyLongPrice) + "," + (lastClose - buyLongPrice) / buyLongPrice * 1000);
                        countTimes(lastClose - buyLongPrice);
                    }
                }
                if ((minuteEma5 - minuteEma10) < 0 && (current1hEma5 - current1hEma10) < 0 && (current4hEma5 - current4hEma10) < 0 && hour4Trend(hourEnd, "down", current4hEma5, current4hEma10)) {
                //if ((minuteEma5 - minuteEma10) < 0 && (current4hEma5 - current4hEma10) < 0) {

                    hour4Trend(hourEnd, "down", current4hEma5, current4hEma10);

                    //判断是否在120分钟内多次买入
                    if (countTimeGapMinutes(candleNew.getCandleTime(), lastBuyTime) < defaultGap) {
                        continue;
                    } else {
                        lastBuyTime = candleNew.getCandleTime();
                    }

                    Double buyShortPrice = getBuyPrice(candleNew.getCandleTime());
                    //计算止损价，取过去5分钟20次的平均值和最低值， 止盈按定时时间来做
                    Double planLossPrice = getLossPrice(candleNew, buyShortPrice, "short");
                    System.out.print(candleNew.getCandleTime() + " " + candleNew.getOpen() + " 适合卖空,买入价：" + buyShortPrice + " 止损价：" + planLossPrice);
                    //由于1分钟数据有限，不能获取当前1分钟的价格，按照接下来5分钟的走势，按1个小时定时止损来看看是否会止损或者退出
                    String tempStart = DateUtils.addMinutes(candleNew.getCandleTime(), 5);
                    String tempEnd = DateUtils.addMinutes(candleNew.getCandleTime(), 65);
                    wrapper = new QueryWrapper<>();
                    wrapper.ge("candle_time", tempStart);
                    wrapper.le("candle_time", tempEnd);
                    wrapper.orderByAsc("candle_time");
                    List<BtcCandles5m> tempList = btcCandles5mMapper.selectList(wrapper);
                    boolean flag = true;
                    for (BtcCandles5m candles5m : tempList) {
                        if (candles5m.getHigh() >= planLossPrice) {
                            System.out.println("达到止损价： 卖出,亏损：" + (buyShortPrice - planLossPrice) + "," + (buyShortPrice - planLossPrice) / buyShortPrice * 1000);
                            flag = false;
                            countTimes(buyShortPrice - planLossPrice);
                            break;
                        }
                    }
                    if (flag) {
                        Double lastClose = tempList.get(tempList.size() - 1).getClose();
                        System.out.println(" 按时卖出： " + lastClose + " 盈亏： " + (buyShortPrice - lastClose) + "," + (buyShortPrice - lastClose) / buyShortPrice * 1000);
                        countTimes(buyShortPrice - lastClose);
                    }
                }
            }
        }

        System.out.println("盈利次数:" + positiveTimes + " 亏损次数: " + negativeTimes);
    }

    private Double getLossPrice(BtcCandles5m candle, Double buyPrice, String type) {
        String end = candle.getCandleTime();
        String start = DateUtils.addMinutes(end, -100);
        QueryWrapper<BtcCandles5m> wrapper = new QueryWrapper<>();
        wrapper.ge("candle_time", start);
        wrapper.le("candle_time", end);
        wrapper.orderByAsc("candle_time");
        List<BtcCandles5m> btcCandles5ms = btcCandles5mMapper.selectList(wrapper);
        Double highest = btcCandles5ms.stream().mapToDouble(BtcCandles5m::getHigh).max().getAsDouble();
        Double lowest = btcCandles5ms.stream().mapToDouble(BtcCandles5m::getLow).min().getAsDouble();
        Double highSum = btcCandles5ms.stream().mapToDouble(BtcCandles5m::getHigh).sum();
        Double lowSum = btcCandles5ms.stream().mapToDouble(BtcCandles5m::getLow).sum();
        Double avgMargin = (highSum - lowSum) / btcCandles5ms.size();
        //计划损失 1.5N的价格
        Double planLoss = 1.5 * avgMargin;
        if (type.equals("long")) {
            return Math.max(buyPrice - planLoss, lowest);
        } else {
            return Math.min(buyPrice + planLoss, highest);
        }
    }

    //获取买入价，一般等于下一个5分钟的开盘价获取下一个一分钟的开盘价
    private Double getBuyPrice(String time) {
        String buyTime = DateUtils.addMinutes(time, 1);
        BtcCandles1m btcCandles1m = btcCandles1mMapper.selectById(buyTime);
        if (btcCandles1m == null) {
            buyTime = DateUtils.addMinutes(time, 5);
            BtcCandles5m btcCandles5m = btcCandles5mMapper.selectById(buyTime);
            return btcCandles5m.getOpen();
        } else {
            return btcCandles1m.getOpen();
        }
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

    private static boolean isDifferentLabel(BtcCandles5m one, BtcCandles5m two) {
        Double oneEmaMargin = one.getEma5() - one.getEma10();
        Double twoEmaMargin = two.getEma5() - two.getEma10();
        if (oneEmaMargin * twoEmaMargin > 0) {
            return false;
        } else {
            return true;
        }
    }

    @SneakyThrows
    private long countTimeGapMinutes(String time1, String time2) {
        Date start = DateUtils.parseUTCTime(time1);
        Date end = DateUtils.parseUTCTime(time2);
        long gap = (end.getTime() - start.getTime()) / 1000;
        long min = gap / 60;
        return Math.abs(min);
    }

    private void countTimes(double value) {
        if (value > 0) {
            positiveTimes++;
        } else {
            negativeTimes++;
        }
    }

    private boolean hour4Trend(String hour4End, String expected, double ema5, double ema10) {
        QueryWrapper<BtcCandles4h> wrapper = new QueryWrapper<>();
        wrapper.lt("candle_time", hour4End);
        wrapper.orderByDesc("candle_time");
        wrapper.last("limit 2");
        List<BtcCandles4h> ethCandles4hs = btcCandles4hMapper.selectList(wrapper);
//        EthCandles4h first =  ethCandles4hs.get(1);
        BtcCandles4h second = ethCandles4hs.get(1);
        if (expected.equals("up")) {
            Double secondGap = second.getEma5() - second.getEma10();
            Double firstGap = ema5 - ema10;
            if ((secondGap - firstGap) > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            Double secondGap = second.getEma10() - second.getEma5();
            Double firstGap = ema10 - ema5;
            if ((secondGap - firstGap) > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

}
