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
import com.think.quantstarter.pilot.bean.OrderRecord;
import com.think.quantstarter.rest.bean.swap.result.*;
import com.think.quantstarter.rest.constant.APIConstants;
import com.think.quantstarter.rest.enums.FuturesTransactionTypeEnum;
import com.think.quantstarter.rest.exception.APIException;
import com.think.quantstarter.utils.DateUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
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

    //记录下单记录，并在到期后卖出
    private static final List<OrderRecord> orderRecords = new ArrayList<>();

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

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(20);
        return taskScheduler;
    }

    /**
     * 当有订单后，开始判断订单是否过期
     * 1. 检查单据是否到期，到期卖出，并从orderRecords删除
     * 2. 检查计划单是否触发，如果触发，从orderRecords删除
     */
    @Scheduled(cron = "0/5 * * * * ?")
    @SneakyThrows
    public void checkAndCleanOrders(){
        if(orderRecords.size()>0){
            while (true){
                List<OrderRecord> removes = new ArrayList<>();
                orderRecords.forEach(orderRecord -> {
                    String buyTime = orderRecord.getTimestamp();
                    long timeGap = countTimeGapMinutesWithNow(buyTime);
                    if(timeGap >= holdTime){
                        //到期卖出
                        log.info("到期卖出....{}",orderRecord);
                        ethTradeService.order(orderRecord.getOrder_type());
                        ethTradeService.cancelOrderAlgo(Collections.singletonList(orderRecord.getAlgo_id()));
                        removes.add(orderRecord);
                    }
                    //检查策略单是否生效
                    SwapOrders swapOrders = ethTradeService.checkAlgoOrder(orderRecord.getAlgo_id());
                    if(swapOrders.getStatus().equals("2")){
                        log.info("止盈止损已经触发....{}",orderRecord);
                        removes.add(orderRecord);
                    }
                });
                orderRecords.removeAll(removes);
                if(orderRecords.size() == 0){
                    break;
                }
                Thread.sleep(1000);
            }
        }
    }

    /**
     * 每隔5分钟去判断是否下单
     */
    @Scheduled(cron = "1 0/5 * * * ?")
    @Retryable(include = {APIException.class}, maxAttempts = 3)
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
                    log.info("interval buy time is less than {}", intervalBuy);
                    return;
                } else {
                    lastBuyTime = candles5mNew.getCandleTime();
                }
                //如果不同，表示在过去一次K线ema有交叉，此策略只按照1h EMA和4h EMA情况下单
                double flag = candles5mNew.getEma5() - candles5mNew.getEma10();
                boolean hour1EmaCheck = hourEmaCheck(ethCandles1h,flag);
                boolean hour4EmaCheck = hour4EmaCheck(ethCandles4h,flag);
                if(flag>0 && hour1EmaCheck && hour4EmaCheck){
                    log.info("check stop times....");
                    if(stopTimes >= limitStopTimes){
                        //避免连续止损，根据测试，一般亏损会连续好几次，这样可以避免不必要的下单，但是也有可能错过买入机会
                        stopTimes++;
                        if(stopTimes == maxStopTimes){
                            stopTimes=0;
                        }
                        return;
                    }
                    //买涨下单
                    log.info("open long....time: {}", candles5mNew.getCandleTime());
                    OrderRecord orderRecord = doOpenOrder(candles5mNew, flag);
                    log.info("order Record: {}",orderRecord);
                    if(orderRecord != null){
                        orderRecords.add(orderRecord);
                    }
                }
                if(flag<0 && hour1EmaCheck && hour4EmaCheck){
                    log.info("check stop times....");
                    if(stopTimes >= limitStopTimes){
                        stopTimes++;
                        if(stopTimes == maxStopTimes){
                            stopTimes=0;
                        }
                        return;
                    }
                    //买跌下单
                    log.info("open short....time: {}", candles5mNew.getCandleTime());
                    OrderRecord orderRecord = doOpenOrder(candles5mNew, flag);
                    log.info("order Record: {}",orderRecord);
                    if(orderRecord != null){
                        orderRecords.add(orderRecord);
                    }
                }
            }
        }catch (APIException e){
            log.error("Get candles has error, {}" ,e.getMessage());
            throw new APIException("Get candles has error");
        }catch (ParseException e){
            log.error("Candle time parse error, {}" ,e.getMessage());
        }
    }

    /**
     * 每隔一段时间去检查orderRecords的持单数量和真实持单数量是否一致，有可能会有多单在手，但是显示一个单据
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void compareOrders(){
        if(orderRecords.size()>0){
            log.info("check real position and procedure!");
            int sumOne = 0;
            int sumTwo = 0;
            for (OrderRecord orderRecord : orderRecords) {
                OrderInfo orderInfo = ethTradeService.getOrderInfo(orderRecord.getOrder_id());
                sumOne = sumOne + Integer.valueOf(orderInfo.getSize());
            }
            PositionVO positionVO = ethTradeService.getPosition();
            List<Position> holding = positionVO.getHolding();
            for (Position position : holding) {
                sumTwo = sumTwo + Integer.valueOf(position.getPosition());
            }
            if(sumOne != sumTwo){
                //如果不一样的话，清空所有订单和orderRecords
                log.error("检查到程序订单和实际订单不一致，清空所有订单信息");
                log.error("orderRecords...{}", orderRecords);
                log.error("实际position 大小: {}", sumTwo);
                ethTradeService.closeAllPositions();
                orderRecords.clear();
            }
            log.info("End checking real position and procedure!");
        }
    }

    /**
     * 定时清理1个月之前的历史数据，防止数据过大
     */
    @Scheduled(cron = "0 0 23 15 * ?")
    public void removeHistoryData(){
        Date now = new Date();
        String nowString = DateUtils.timeToString(now, 8);
        String monthAgo = DateUtils.addMinutes(nowString, -60 * 24 * 30);
        QueryWrapper<EthCandles5m> ethCandles5mWrapper = new QueryWrapper<>();
        ethCandles5mWrapper.le("candle_time", monthAgo);
        ethCandles5mService.remove(ethCandles5mWrapper);
        QueryWrapper<EthCandles1h> ethCandles1hQueryWrapper = new QueryWrapper<>();
        ethCandles5mWrapper.le("candle_time", monthAgo);
        ethCandles1hService.remove(ethCandles1hQueryWrapper);
        QueryWrapper<EthCandles4h> ethCandles4hWrapper = new QueryWrapper<>();
        ethCandles4hWrapper.le("candle_time", monthAgo);
        ethCandles4hService.remove(ethCandles4hWrapper);
    }

    /**
     * 需要返回一个记录order 信息的BO，用来记录order_id，开单均价， FuturesTransactionTypeEnum， 止盈止损单的ID等信息
     * 未考虑下单失败的情况
     * @param flag
     */
    @SneakyThrows
    private OrderRecord doOpenOrder(EthCandles5m ethCandles5m, double flag){
        String order_id = "";
        String price_avg = "";
        String algo_id = "";
        String timeStamp = "";
        FuturesTransactionTypeEnum order_type;
        Map<String, Double> lossAndGainPrice;
        try{
            if(flag>0){
                //open long
                order_type = FuturesTransactionTypeEnum.CLOSE_LONG;
                PerOrderResult order = ethTradeService.order(FuturesTransactionTypeEnum.OPEN_LONG);
                order_id = order.getOrder_id();
                OrderInfo orderInfo = ethTradeService.getOrderInfo(order_id);
                price_avg = orderInfo.getPrice_avg();
                timeStamp = orderInfo.getTimestamp();
                lossAndGainPrice = getLossAndGainPrice(ethCandles5m, Double.valueOf(price_avg), flag);
                SwapOrderResultVO swapOrderResultVO = ethTradeService.swapOrderAlgo(FuturesTransactionTypeEnum.CLOSE_LONG, lossAndGainPrice.get("gainPrice").toString(), lossAndGainPrice.get("lossPrice").toString());
                algo_id = swapOrderResultVO.getData().getAlgo_id();
            }else {
                //open short
                order_type = FuturesTransactionTypeEnum.CLOSE_SHORT;
                PerOrderResult order = ethTradeService.order(FuturesTransactionTypeEnum.OPEN_SHORT);
                order_id = order.getOrder_id();
                OrderInfo orderInfo = ethTradeService.getOrderInfo(order_id);
                price_avg = orderInfo.getPrice_avg();
                timeStamp = orderInfo.getTimestamp();
                lossAndGainPrice = getLossAndGainPrice(ethCandles5m, Double.valueOf(price_avg), flag);
                SwapOrderResultVO swapOrderResultVO = ethTradeService.swapOrderAlgo(FuturesTransactionTypeEnum.CLOSE_SHORT, lossAndGainPrice.get("gainPrice").toString(), lossAndGainPrice.get("lossPrice").toString());
                algo_id = swapOrderResultVO.getData().getAlgo_id();
            }
        }catch (APIException e){
            log.error("下单失败，取消订单和止盈止损单...");
            if(Strings.isNotEmpty(order_id)){
                ethTradeService.cancelOrder(order_id);
            }
            if(Strings.isNotEmpty(algo_id)){
                ethTradeService.cancelOrderAlgo(Collections.singletonList(algo_id));
            }
            return null;
        }
        OrderRecord build = OrderRecord.builder()
                .order_id(order_id)
                .algo_id(algo_id)
                .timestamp(timeStamp)
                .order_type(order_type)
                .build();
        return build;
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

    //每次cronjob完成后，需要检查最新一条数据是否是最近一条5m的数据, 最近一条并不是最新的那条，最新的那条不计入EMA计算
    @SneakyThrows
    private boolean checkCandle5mTime(EthCandles5m candles5mNew) {
        String candleTimeString = candles5mNew.getCandleTime();
        Date candleTime = DateUtils.parseUTCTime(candleTimeString);
        Date timeNow = new Date();
        long minutes = Duration.between(candleTime.toInstant(), timeNow.toInstant()).toMinutes();
        if(minutes == 5){
            return true;
        }else{
            log.warn("candles5mNew gap with current: [{}], {}, {}", minutes, candleTimeString,timeNow);
            return false;
        }
    }

    //每次cronjob完成后，需要检查最新一条数据是否是最近一条5m的数据, 最近一条并不是最新的那条，最新的那条不计入EMA计算
    @SneakyThrows
    private void checkCandle5mTime(String candleTimeString) {
        Date candleTime = DateUtils.parseUTCTime(candleTimeString);
        Date timeNow = new Date();
        long minutes = Duration.between(candleTime.toInstant(), timeNow.toInstant()).toMinutes();
        if(minutes != 0){
            log.warn("The gap of candles5m latest time and now is not zero, will retry: [{}], {}, {}", minutes, candleTimeString,timeNow);
            throw new APIException("The gap of candles5m latest time and now is not zero, will retry");
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

    @SneakyThrows
    private long countTimeGapMinutesWithNow(String time1) {
        Date start = DateUtils.parseUTCTime(time1);
        Date end = new Date();
        long gap = (end.getTime() - start.getTime()) / 1000;
        long min = gap / 60;
        return Math.abs(min);
    }

    private void getCandles(String granularity, Class clz, IService service){
        JSONArray candles = candlesService.getCandles(eth_instrument_id, granularity, get_candle_records);
        List<Object> objectList = ConvertToObjectUtil.convertJsonArrayToObjects(candles, clz);
        if(APIConstants.GRANULARITY5MIN.equals(granularity)){
            EthCandles5m ethCandles5m = (EthCandles5m) objectList.get(0);
            checkCandle5mTime(ethCandles5m.getCandleTime());
        }
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
