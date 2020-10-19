package com.think.quantstarter.pilot.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.think.quantstarter.dataCollect.entity.EthCandles1h;
import com.think.quantstarter.dataCollect.entity.EthCandles4h;
import com.think.quantstarter.dataCollect.entity.EthCandles5m;
import com.think.quantstarter.dataCollect.manager.ICandlesService;
import com.think.quantstarter.dataCollect.service.IEthCandles1hService;
import com.think.quantstarter.dataCollect.service.IEthCandles4hService;
import com.think.quantstarter.dataCollect.service.IEthCandles5mService;
import com.think.quantstarter.dataCollect.utils.ConvertToObjectUtil;
import com.think.quantstarter.rest.bean.swap.result.PerOrderResult;
import com.think.quantstarter.rest.constant.APIConstants;
import com.think.quantstarter.rest.enums.FuturesTransactionTypeEnum;
import com.think.quantstarter.rest.exception.APIException;
import com.think.quantstarter.utils.DateUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * @author mpthink
 * @date 2020/10/10 16:09
 */
@Service
@Slf4j
public class EthTradePilotService {

    private static final String eth_instrument_id = "ETH-USDT-SWAP";
    private static final Integer get_candle_records = 20;
    private static final Integer intervalBuy = 120;
    private static final Integer holdTime = 180;
    private static String lastBuyTime = "2020-10-09T00:40:00.000Z";

    private static int stopTimes = 0;
    private static final int limitStopTimes = 2;
    private static final int maxStopTimes = 6;
    //过去一段时间的K线均值
    private static double avgMargin = 0;
    //计划损失的avgMargin的倍数
    private static final int lossN = 2;
    //获取过去多久的K线值，当前时间往后多久
    private static final int lossMinutes = 60;
    //计划盈利的avgMargin的倍数
    private static final int gainN = 6;

    @Resource
    private ICandlesService candlesService;
    @Resource
    private IEthCandles5mService ethCandles5mService;
    @Resource
    private IEthCandles1hService ethCandles1hService;
    @Resource
    private IEthCandles4hService ethCandles4hService;
    @Resource
    private EthEmaGenerator ethEmaGenerator;
    @Resource
    private EthTradeService ethTradeService;

    //@Scheduled(cron = "1 0/5 * * * ?")
    //@Retryable(include = {APIException.class}, maxAttempts = 3)
    public void openOrder(){
        try{
            //获取最新的candle数据
            getCandles(APIConstants.GRANULARITY5MIN, EthCandles5m.class, ethCandles5mService);
            getCandles(APIConstants.GRANULARITY1HOUR, EthCandles1h.class, ethCandles1hService);
            getCandles(APIConstants.GRANULARITY4HOUR, EthCandles4h.class, ethCandles4hService);
            //计算ema值
            Map<String, EthCandles5m> resultMap = ethEmaGenerator.generateEma5m();
            EthCandles5m candles5mNew = resultMap.get("new");
            EthCandles5m candles5mOld = resultMap.get("old");
            EthCandles1h ethCandles1h = ethEmaGenerator.generateEma1h(getHourStart(candles5mNew.getCandleTime()));
            EthCandles4h ethCandles4h = ethEmaGenerator.generateEma4h(get4HourStart(candles5mNew.getCandleTime()));
            if(!checkCandle5mTime(candles5mNew)){
                throw new APIException("Candle 5m is not expected time!");
            }
            //开始条件判断并下单
            if(isDifferentLabel(candles5mOld, candles5mNew)){
                //判断是否ema5和ema10交替的时间间隔是否大于given intervalBuy，这个条件一定程度上可以消除频繁震荡带来的下单风险
                //本想设置购买间隔的，但是发现该参数对于结果更好，误打误撞的一个参数
                if (countTimeGapMinutes(candles5mNew.getCandleTime(), lastBuyTime) < intervalBuy) {
                    return;
                } else {
                    lastBuyTime = candles5mNew.getCandleTime();
                }
                //如果不同，表示在过去一次K线ema有交叉，此策略只按照1h EMA和4h EMA情况下单
                double flag = candles5mNew.getEma5() - candles5mNew.getEma10();
                boolean hour1EmaCheck = hourEmaCheck(ethCandles1h,flag);
                boolean hour4EmaCheck = hour4EmaCheck(ethCandles4h,flag);
                if(flag>0 && hour1EmaCheck && hour4EmaCheck){
                    if(stopTimes >= limitStopTimes){
                        //避免连续止损，根据测试，一般亏损会连续好几次，这样可以避免不必要的下单，但是也有可能错过买入机会
                        stopTimes++;
                        if(stopTimes == maxStopTimes){
                            stopTimes=0;
                        }
                        return;
                    }
                    //买涨下单
                }
                if(flag<0 && hour1EmaCheck && hour4EmaCheck){
                    if(stopTimes >= limitStopTimes){
                        stopTimes++;
                        if(stopTimes == maxStopTimes){
                            stopTimes=0;
                        }
                        return;
                    }
                    //买跌下单
                }
            }
        }catch (APIException e){
            log.error("Get candles has error, {}" ,e.getMessage());
            throw new APIException("Get candles has error");
        }catch (ParseException e){
            log.error("Candle time parse error, {}" ,e.getMessage());
        }
    }

    private void doOpenOrder(double flag){
        if(flag>0){
            //open long
            PerOrderResult order = ethTradeService.order(FuturesTransactionTypeEnum.OPEN_LONG);
        }else {
            //open short
            PerOrderResult order = ethTradeService.order(FuturesTransactionTypeEnum.OPEN_SHORT);
        }
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

    private static boolean hourEmaCheck(EthCandles1h ethCandles1h, double flag){
        if (flag > 0) {
            return (ethCandles1h.getEma5() - ethCandles1h.getEma10()) > 0;
        } else {
            return (ethCandles1h.getEma5() - ethCandles1h.getEma10()) < 0;
        }
    }

    private static boolean hour4EmaCheck(EthCandles4h ethCandles4h, double flag){
        if (flag > 0) {
            return (ethCandles4h.getEma5() - ethCandles4h.getEma10()) > 0;
        } else {
            return (ethCandles4h.getEma5() - ethCandles4h.getEma10()) < 0;
        }
    }

    //每次cronjob完成后，需要检查最新一条数据是否是最近一条5m的数据
    @SneakyThrows
    private boolean checkCandle5mTime(EthCandles5m candles5mNew) {
        String candleTimeString = candles5mNew.getCandleTime();
        Date candleTime = DateUtils.parseUTCTime(candleTimeString);
        Date timeNow = new Date();
        Duration.between(candleTime.toInstant(), timeNow.toInstant()).toMinutes();

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = candleTime.toInstant().atZone(zoneId).toLocalDateTime();
        LocalDateTime temp1 = localDateTime.plusMinutes(5);
        LocalDateTime temp2 = LocalDateTime.now();
        if(temp1.getMinute() == temp2.getMinute()){
            return true;
        }else{
            return false;
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

    private void getCandles(String granularity, Class clz, IService service){
        JSONArray candles = candlesService.getCandles(eth_instrument_id, granularity, get_candle_records);
        List<Object> objectList = ConvertToObjectUtil.convertJsonArrayToObjects(candles, clz);
        service.saveOrUpdateBatch(objectList);
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

    private static String getHourStart(String candleTime) {
        return DateUtils.getUTCWithoutMinutes(candleTime);
    }

    //获取止损价和止盈价
    private Map<String,Double> getLossAndGainPrice(EthCandles5m candle, Double buyPrice, double flag) {
        String end = candle.getCandleTime();
        String start = DateUtils.addMinutes(end, -lossMinutes);
        QueryWrapper<EthCandles5m> wrapper = new QueryWrapper<>();
        wrapper.ge("candle_time", start);
        wrapper.le("candle_time", end);
        wrapper.orderByAsc("candle_time");
        List<EthCandles5m> ethCandles5ms = ethCandles5mService.list(wrapper);
        Double highest = ethCandles5ms.stream().mapToDouble(EthCandles5m::getHigh).max().getAsDouble();
        Double lowest = ethCandles5ms.stream().mapToDouble(EthCandles5m::getLow).min().getAsDouble();
        Double highSum = ethCandles5ms.stream().mapToDouble(EthCandles5m::getHigh).sum();
        Double lowSum = ethCandles5ms.stream().mapToDouble(EthCandles5m::getLow).sum();
        Double avgMargin = (highSum - lowSum) / ethCandles5ms.size();
        Map<String,Double> map = new HashMap<>();
        Double planLoss = lossN * avgMargin;
        if (flag > 0) {
            //做多
            map.put("lossPrice", Math.max(buyPrice - planLoss, lowest));
            map.put("gainPrice", buyPrice + gainN * avgMargin);
        } else {
            //做空
            map.put("lossPrice", Math.min(buyPrice + planLoss, highest));
            map.put("gainPrice", buyPrice - gainN * avgMargin);
        }
        return map;
    }

}
