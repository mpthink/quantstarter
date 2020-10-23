package com.think.quantstarter.pilot.service.bch;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.think.quantstarter.dataCollect.entity.BchCandles1h;
import com.think.quantstarter.dataCollect.entity.BchCandles4h;
import com.think.quantstarter.dataCollect.entity.BchCandles5m;
import com.think.quantstarter.dataCollect.manager.ICandlesService;
import com.think.quantstarter.dataCollect.service.IBchCandles1hService;
import com.think.quantstarter.dataCollect.service.IBchCandles4hService;
import com.think.quantstarter.dataCollect.service.IBchCandles5mService;
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
public class BchTradePilotService {

    private static final String bch_instrument_id = "BCH-USDT-SWAP";
    private static final Integer get_candle_records = 20;
    private static final Integer intervalBuy = 60;
    private static final Integer holdTime = 240;
    private static String lastBuyTime = "2020-10-09T00:40:00.000Z";

    private static int stopTimes = 0;
    private static final int limitStopTimes = 2;
    private static final int maxStopTimes = 6;
    //计划损失的avgMargin的倍数
    private static final int lossN = 5;
    //获取过去多久的K线值，当前时间往后多久
    private static final int lossMinutes = 120;
    //计划盈利的avgMargin的倍数
    private static final int gainN = 5;

    //记录下单记录，并在到期后卖出
    private static final List<OrderRecord> orderRecords = new ArrayList<>();

    @Resource
    private ICandlesService candlesService;
    @Resource
    private IBchCandles5mService bchCandles5mService;
    @Resource
    private IBchCandles1hService bchCandles1hService;
    @Resource
    private IBchCandles4hService bchCandles4hService;
    @Resource
    private BchEmaGenerator bchEmaGenerator;
    @Resource
    private BchTradeService bchTradeService;

    /**
     * 当有订单后，开始判断订单是否过期
     * 1. 检查单据是否到期，到期卖出，并从orderRecords删除
     * 2. 检查计划单是否触发，如果触发，从orderRecords删除
     */
    //@Scheduled(cron = "0/5 * * * * ?")
    @SneakyThrows
    public void checkAndCleanOrders(){
        if(orderRecords.size()>0){
            while (true){
                List<OrderRecord> removes = new ArrayList<>();
                for (OrderRecord orderRecord : orderRecords) {//检查策略单是否生效
                    SwapOrders swapOrders = bchTradeService.checkAlgoOrder(orderRecord.getAlgo_id());
                    if (swapOrders.getStatus().equals("2") || swapOrders.getStatus().equals("3")) {
                        log.info("BCH止盈止损已经触发，或者超时取消....{}", orderRecord);
                        removes.add(orderRecord);
                        continue;
                    }
                    String buyTime = orderRecord.getTimestamp();
                    long timeGap = countTimeGapMinutesWithNow(buyTime);
                    if (timeGap >= holdTime) {
                        //到期卖出
                        log.info("BCH到期卖出....{}", orderRecord);
                        bchTradeService.order(orderRecord.getOrder_type());
                        bchTradeService.cancelOrderAlgo(Collections.singletonList(orderRecord.getAlgo_id()));
                        removes.add(orderRecord);
                    }
                }
                orderRecords.removeAll(removes);
                if(orderRecords.size() == 0){
                    break;
                }
                Thread.sleep(2000);
            }
        }
    }

    /**
     * 每隔5分钟去判断是否下单
     */
    //@Scheduled(cron = "1 0/5 * * * ?")
    //@Retryable(include = {APIException.class}, maxAttempts = 3)
    public void openOrder(){
        try{
            //获取最新的candle数据
            getCandles(APIConstants.GRANULARITY5MIN, BchCandles5m.class, bchCandles5mService);
            //计算ema值
            Map<String, BchCandles5m> resultMap = bchEmaGenerator.generateEma5m();
            BchCandles5m candles5mNew = resultMap.get("new");
            BchCandles5m candles5mOld = resultMap.get("old");
            //开始条件判断并下单
            if(isDifferentLabel(candles5mOld, candles5mNew)){
                //判断是否ema5和ema10交替的时间间隔是否大于given intervalBuy，这个条件一定程度上可以消除频繁震荡带来的下单风险
                //本想设置购买间隔的，但是发现该参数对于结果更好，误打误撞的一个参数
                if (countTimeGapMinutes(candles5mNew.getCandleTime(), lastBuyTime) < intervalBuy) {
                    log.info("BCH interval buy time is less than {}", intervalBuy);
                    return;
                } else {
                    lastBuyTime = candles5mNew.getCandleTime();
                }
                //如果不同，表示在过去一次K线ema有交叉，此策略只按照1h EMA和4h EMA情况下单
                double flag = candles5mNew.getEma5() - candles5mNew.getEma10();
                if(flag>0){
                    log.info("BCH check stop times....");
                    if(stopTimes >= limitStopTimes){
                        //避免连续止损，根据测试，一般亏损会连续好几次，这样可以避免不必要的下单，但是也有可能错过买入机会
                        stopTimes++;
                        if(stopTimes == maxStopTimes){
                            stopTimes=0;
                        }
                        return;
                    }
                    //买涨下单
                    log.info("BCH open long....time: {}", candles5mNew.getCandleTime());
                    OrderRecord orderRecord = doOpenOrder(candles5mNew, flag);
                    log.info("BCH order Record: {}",orderRecord);
                    if(orderRecord != null){
                        orderRecords.add(orderRecord);
                    }
                }
                if(flag<0){
                    log.info("BCH check stop times....");
                    if(stopTimes >= limitStopTimes){
                        stopTimes++;
                        if(stopTimes == maxStopTimes){
                            stopTimes=0;
                        }
                        return;
                    }
                    //买跌下单
                    log.info("BCH open short....time: {}", candles5mNew.getCandleTime());
                    OrderRecord orderRecord = doOpenOrder(candles5mNew, flag);
                    log.info("BCH order Record: {}",orderRecord);
                    if(orderRecord != null){
                        orderRecords.add(orderRecord);
                    }
                }
            }
        }catch (APIException e){
            log.error("Get candles has error, {}" ,e.getMessage());
            throw new APIException("Get candles has error");
        }
    }

    /**
     * 每隔一段时间去检查orderRecords的持单数量和真实持单数量是否一致，有可能会有多单在手，但是显示一个单据
     */
    //@Scheduled(cron = "0 0 8 * * ?")
    public void compareOrders(){
        if(orderRecords.size()>0){
            log.info("BCH check real position and procedure!");
            int sumOne = 0;
            int sumTwo = 0;
            for (OrderRecord orderRecord : orderRecords) {
                OrderInfo orderInfo = bchTradeService.getOrderInfo(orderRecord.getOrder_id());
                sumOne = sumOne + Integer.valueOf(orderInfo.getSize());
            }
            PositionVO positionVO = bchTradeService.getPosition();
            List<Position> holding = positionVO.getHolding();
            for (Position position : holding) {
                sumTwo = sumTwo + Integer.valueOf(position.getPosition());
            }
            if(sumOne != sumTwo){
                //如果不一样的话，清空所有订单和orderRecords
                log.error("BCH 检查到程序订单和实际订单不一致，清空所有订单信息");
                log.error("BCH orderRecords...{}", orderRecords);
                log.error("BCH 实际position 大小: {}", sumTwo);
                bchTradeService.closeAllPositions();
                orderRecords.clear();
            }
            log.info("BCH End checking real position and procedure!");
        }
    }

    /**
     * 定时清理1个月之前的历史数据，防止数据过大
     */
    //@Scheduled(cron = "0 0 23 15 * ?")
    public void removeHistoryData(){
        Date now = new Date();
        String nowString = DateUtils.timeToString(now, 8);
        String monthAgo = DateUtils.addMinutes(nowString, -60 * 24 * 30);
        QueryWrapper<BchCandles5m> bchCandles5mWrapper = new QueryWrapper<>();
        bchCandles5mWrapper.le("candle_time", monthAgo);
        bchCandles5mService.remove(bchCandles5mWrapper);
        QueryWrapper<BchCandles1h> bchCandles1hQueryWrapper = new QueryWrapper<>();
        bchCandles5mWrapper.le("candle_time", monthAgo);
        bchCandles1hService.remove(bchCandles1hQueryWrapper);
        QueryWrapper<BchCandles4h> bchCandles4hWrapper = new QueryWrapper<>();
        bchCandles4hWrapper.le("candle_time", monthAgo);
        bchCandles4hService.remove(bchCandles4hWrapper);
    }

    /**
     * 需要返回一个记录order 信息的BO，用来记录order_id，开单均价， FuturesTransactionTypeEnum， 止盈止损单的ID等信息
     * 未考虑下单失败的情况
     * @param flag
     */
    @SneakyThrows
    private OrderRecord doOpenOrder(BchCandles5m bchCandles5m, double flag){
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
                PerOrderResult order = bchTradeService.order(FuturesTransactionTypeEnum.OPEN_LONG);
                order_id = order.getOrder_id();
                OrderInfo orderInfo = bchTradeService.getOrderInfo(order_id);
                price_avg = orderInfo.getPrice_avg();
                timeStamp = orderInfo.getTimestamp();
                lossAndGainPrice = getLossAndGainPrice(bchCandles5m, Double.valueOf(price_avg), flag);
                SwapOrderResultVO swapOrderResultVO = bchTradeService.swapOrderAlgo(FuturesTransactionTypeEnum.CLOSE_LONG, lossAndGainPrice.get("gainPrice").toString(), lossAndGainPrice.get("lossPrice").toString());
                algo_id = swapOrderResultVO.getData().getAlgo_id();
            }else {
                //open short
                order_type = FuturesTransactionTypeEnum.CLOSE_SHORT;
                PerOrderResult order = bchTradeService.order(FuturesTransactionTypeEnum.OPEN_SHORT);
                order_id = order.getOrder_id();
                OrderInfo orderInfo = bchTradeService.getOrderInfo(order_id);
                price_avg = orderInfo.getPrice_avg();
                timeStamp = orderInfo.getTimestamp();
                lossAndGainPrice = getLossAndGainPrice(bchCandles5m, Double.valueOf(price_avg), flag);
                SwapOrderResultVO swapOrderResultVO = bchTradeService.swapOrderAlgo(FuturesTransactionTypeEnum.CLOSE_SHORT, lossAndGainPrice.get("gainPrice").toString(), lossAndGainPrice.get("lossPrice").toString());
                algo_id = swapOrderResultVO.getData().getAlgo_id();
            }
        }catch (APIException e){
            log.error("BCH 下单失败，取消订单和止盈止损单...");
            if(Strings.isNotEmpty(order_id)){
                bchTradeService.cancelOrder(order_id);
            }
            if(Strings.isNotEmpty(algo_id)){
                bchTradeService.cancelOrderAlgo(Collections.singletonList(algo_id));
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




    private static boolean isDifferentLabel(BchCandles5m one, BchCandles5m two) {
        Double oneEmaMargin = one.getEma5() - one.getEma10();
        Double twoEmaMargin = two.getEma5() - two.getEma10();
        if (oneEmaMargin * twoEmaMargin > 0) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean hourEmaCheck(BchCandles1h bchCandles1h, double flag){
        if (flag > 0) {
            return (bchCandles1h.getEma5() - bchCandles1h.getEma10()) > 0;
        } else {
            return (bchCandles1h.getEma5() - bchCandles1h.getEma10()) < 0;
        }
    }

    private static boolean hour4EmaCheck(BchCandles4h bchCandles4h, double flag){
        if (flag > 0) {
            return (bchCandles4h.getEma5() - bchCandles4h.getEma10()) > 0;
        } else {
            return (bchCandles4h.getEma5() - bchCandles4h.getEma10()) < 0;
        }
    }

    //每次cronjob完成后，需要检查最新一条数据是否是最近一条5m的数据, 最近一条并不是最新的那条，最新的那条不计入EMA计算
    @SneakyThrows
    private boolean checkCandle5mTime(BchCandles5m candles5mNew) {
        String candleTimeString = candles5mNew.getCandleTime();
        Date candleTime = DateUtils.parseUTCTime(candleTimeString);
        Date timeNow = new Date();
        long minutes = Duration.between(candleTime.toInstant(), timeNow.toInstant()).toMinutes();
        if(minutes == 5){
            return true;
        }else{
            log.warn("BCH candles5mNew gap with current: [{}], {}, {}", minutes, candleTimeString,timeNow);
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
            log.warn("BCH The gap of candles5m latest time and now is not zero, will retry: [{}], {}, {}", minutes, candleTimeString,timeNow);
            throw new APIException("BCH The gap of candles5m latest time and now is not zero, will retry");
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
        JSONArray candles = candlesService.getCandles(bch_instrument_id, granularity, get_candle_records);
        List<Object> objectList = ConvertToObjectUtil.convertJsonArrayToObjects(candles, clz);
        if(APIConstants.GRANULARITY5MIN.equals(granularity)){
            BchCandles5m bchCandles5m = (BchCandles5m) objectList.get(0);
            checkCandle5mTime(bchCandles5m.getCandleTime());
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
    private Map<String,Double> getLossAndGainPrice(BchCandles5m candle, Double buyPrice, double flag) {
        String end = candle.getCandleTime();
        String start = DateUtils.addMinutes(end, -lossMinutes);
        QueryWrapper<BchCandles5m> wrapper = new QueryWrapper<>();
        wrapper.ge("candle_time", start);
        wrapper.le("candle_time", end);
        wrapper.orderByAsc("candle_time");
        List<BchCandles5m> bchCandles5ms = bchCandles5mService.list(wrapper);
        Double highest = bchCandles5ms.stream().mapToDouble(BchCandles5m::getHigh).max().getAsDouble();
        Double lowest = bchCandles5ms.stream().mapToDouble(BchCandles5m::getLow).min().getAsDouble();
        Double highSum = bchCandles5ms.stream().mapToDouble(BchCandles5m::getHigh).sum();
        Double lowSum = bchCandles5ms.stream().mapToDouble(BchCandles5m::getLow).sum();
        Double avgMargin = (highSum - lowSum) / bchCandles5ms.size();
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
