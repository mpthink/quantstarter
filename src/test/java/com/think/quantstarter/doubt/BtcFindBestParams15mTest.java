package com.think.quantstarter.doubt;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.think.quantstarter.analysis.SelectConditions;
import com.think.quantstarter.analysis.utils.CandleUtil;
import com.think.quantstarter.dataCollect.entity.BtcCandles15m;
import com.think.quantstarter.dataCollect.entity.BtcCandles1h;
import com.think.quantstarter.dataCollect.entity.BtcCandles4h;
import com.think.quantstarter.dataCollect.mapper.BtcCandles15mMapper;
import com.think.quantstarter.dataCollect.mapper.BtcCandles1hMapper;
import com.think.quantstarter.dataCollect.mapper.BtcCandles4hMapper;
import com.think.quantstarter.utils.DateUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author mpthink
 * @date 2020/9/23 18:56
 */
@SpringBootTest
@Slf4j
public class BtcFindBestParams15mTest {

    @Resource
    private BtcCandles15mMapper btcCandles15mMapper;
    @Resource
    private BtcCandles1hMapper btcCandles1hMapper;
    @Resource
    private BtcCandles4hMapper btcCandles4hMapper;

    //需要随机的参数，以数组存放，用一个map来存储参数组合和
    //计划损失N的倍数
    private static Double[] lossNArray = new Double[]{1.0, 2.0, 3.0, 4.0, 5.0};
    //最大损失取过去多少分钟的
    private static Integer[] lossMArray = new Integer[]{60, 90, 120};
    //最长持有多久
    private static Integer[] handMArray = new Integer[]{65, 125, 185, 245};
    //条件判断随机
    private static Boolean[] booleanArray = new Boolean[]{true, false};
    //震荡时间间隔，此处不考虑是否下单，仅考虑震荡时长，在频繁震荡中购买次数会很少
    private static Integer[] intervalArray = new Integer[]{60, 120, 180, 240};
    private static Integer[] maxStopTimes = new Integer[]{4, 5, 6, 7, 8};
    //盈利倍数，当前振幅的N倍
    private static Integer[] gainLossAVG = new Integer[]{4, 5, 6, 8, 10};

    //存储条件和结果
    private static Map<SelectConditions, Double> resultMap = new HashMap<>();


    private final static int defaultStopTimes = 2;


    @Test
    public void testMaxMap() {
        while (true) {
            double lossN = (double) getRanInArr(lossNArray);
            int lossM = (int) getRanInArr(lossMArray);
            boolean hour1Ema = (boolean) getRanInArr(booleanArray);
            boolean hour4Ema = (boolean) getRanInArr(booleanArray);
            int handInTime = (int) getRanInArr(handMArray);
            int intervalBuy = (int) getRanInArr(intervalArray);
            int maxStop = (int) getRanInArr(maxStopTimes);
            int gainAVG = (int) getRanInArr(gainLossAVG);
            SelectConditions selectConditions = SelectConditions.builder()
                    .lossN(lossN).lossM(lossM)
                    .hour1Ema(hour1Ema).hour4Ema(hour4Ema)
                    .maxStopTimes(maxStop)
                    .gainAVG(gainAVG)
                    .handInTime(handInTime).intervalBuy(intervalBuy).build();
            resultMap.put(selectConditions, 0.0);
            System.out.println(resultMap.size());
        }
    }

    @Test
    public void testOne() {
        SelectConditions selectConditions = SelectConditions.builder()
                .lossN(2).lossM(60)
                .hour1Ema(true).hour4Ema(true)
                .maxStopTimes(8)
                .gainAVG(8)
                .handInTime(245).intervalBuy(240).build();
        countStrategy(selectConditions);
    }


    @SneakyThrows
    @Test
    public void findMax() {
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = new ThreadPoolExecutor(cores, cores, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        //countStrategy(1.0, 40, true, true, false, false,125,0);
        int expectedSize = 24000;
        while (true) {
            double lossN = (double) getRanInArr(lossNArray);
            int lossM = (int) getRanInArr(lossMArray);
            boolean hour1Ema = (boolean) getRanInArr(booleanArray);
            boolean hour4Ema = (boolean) getRanInArr(booleanArray);
            int maxStop = (int) getRanInArr(maxStopTimes);
            int handInTime = (int) getRanInArr(handMArray);
            int intervalBuy = (int) getRanInArr(intervalArray);
            int gainAVG = (int) getRanInArr(gainLossAVG);
            SelectConditions selectConditions = SelectConditions.builder()
                    .lossN(lossN).lossM(lossM)
                    .hour1Ema(hour1Ema).hour4Ema(hour4Ema)
                    .maxStopTimes(maxStop)
                    .gainAVG(gainAVG)
                    .handInTime(handInTime).intervalBuy(intervalBuy).build();
            resultMap.put(selectConditions, 0.0);
            if (resultMap.size() >= expectedSize) {
                break;
            }
        }

        resultMap.keySet().forEach(selectConditions -> {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    countStrategy(selectConditions);
                }
            });
        });

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            Thread.sleep(1000);
        }
        printMaxResult();

    }


    /**
     * 如果随机到的boolean值为负，表示不使用该条件，那么这个条件的值会在方法里面赋值为true
     */
    @SneakyThrows
    public void countStrategy(SelectConditions condition) {
        //获取最早一条记录
        //log.info("Begin.....................................................................");

        int gainTimes = 0;
        int lossTimes = 0;
        int stopTimes = 0;
        double lossAVG;

        double lossN = condition.getLossN();
        int lossM = condition.getLossM();
        boolean hour1Ema = condition.isHour1Ema();
        boolean hour4Ema = condition.isHour4Ema();
        int maxStop = condition.getMaxStopTimes();
        int gainAVG = condition.getGainAVG();
        int handInTime = condition.getHandInTime();
        int intervalBuy = condition.getIntervalBuy();

        QueryWrapper<BtcCandles15m> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("candle_time");
        wrapper.eq("candle_time","2020-09-08T08:00:00.000Z");
        wrapper.last("limit 1");
        BtcCandles15m oldest = btcCandles15mMapper.selectOne(wrapper);
        String start = oldest.getCandleTime();
        String end = start;
        String lastBuyTime = start;
        int i = 0;
        final List<Double> sum = new ArrayList<>();
        while (DateUtils.parseUTCTime(end).before(new Date())) {
            end = DateUtils.addMinutes(start, 15);
            wrapper = new QueryWrapper<>();
            wrapper.ge("candle_time", start);
            wrapper.le("candle_time", end);
            wrapper.orderByAsc("candle_time");
            start = end;
            //第一条数据不做分析
            if (i == 0) {
                i++;
                continue;
            }
            List<BtcCandles15m> btcCandles15ms = btcCandles15mMapper.selectList(wrapper);
            if (btcCandles15ms.size() != 2) {
                //System.out.println("No enough data for analysis!");
                log.info("Finish............" + condition + ",sum: " + sum.stream().mapToDouble(Double::doubleValue).sum() + "," + sum.size() + "," + gainTimes + "," + lossTimes);
                //准备写入map中
                resultMap.put(condition, sum.stream().mapToDouble(Double::doubleValue).sum());
                return;
            }
            BtcCandles15m candleOld = btcCandles15ms.get(0);
            BtcCandles15m candleNew = btcCandles15ms.get(1);
            //判断前一个15m的ema5-ema10和当前ema5-ema10值是否相反
            if (isDifferentLabel(candleOld, candleNew)) {
                Double minuteEma5 = candleNew.getEma5();
                Double minuteEma10 = candleNew.getEma10();
                double flag = minuteEma5 - minuteEma10;
                boolean hour1EmaC = hour1Ema ? hourEmaCheck(candleNew, flag) : true;
                boolean hour4EmaC = hour4Ema ? hour4EmaCheck(candleNew, flag) : true;

                Double buyPrice = getBuyPrice(candleNew.getCandleTime());
                //判断是否在120分钟内多次买入
                if (countTimeGapMinutes(candleNew.getCandleTime(), lastBuyTime) < intervalBuy) {
                    continue;
                } else {
                    lastBuyTime = candleNew.getCandleTime();
                }

                if (flag > 0 && hour1EmaC && hour4EmaC) {
                    //买涨
                    if (stopTimes >= defaultStopTimes) {
                        stopTimes++;
                        if (stopTimes == maxStop) {
                            stopTimes = 0;
                        }
                        continue;
                    }

                    Map<String, Double> doubleMap = getLossPrice(candleNew, buyPrice, flag, lossN, lossM);
                    Double planLossPrice = doubleMap.get("lossPrice");
                    lossAVG = doubleMap.get("lossAVG");
                    //begin
                    String tempStart = DateUtils.addMinutes(candleNew.getCandleTime(), 5);
                    String tempEnd = DateUtils.addMinutes(candleNew.getCandleTime(), handInTime);
                    QueryWrapper<BtcCandles15m> queryWrapper = new QueryWrapper<>();
                    queryWrapper.ge("candle_time", tempStart);
                    queryWrapper.le("candle_time", tempEnd);
                    queryWrapper.orderByAsc("candle_time");
                    List<BtcCandles15m> tempList = btcCandles15mMapper.selectList(queryWrapper);
                    boolean sellflag = true;
                    if (flag > 0) {
                        for (BtcCandles15m candles15m : tempList) {
                            if (candles15m.getLow() <= planLossPrice) {
                                double loss = planLossPrice - buyPrice;
                                sum.add(loss / buyPrice * 1000);
                                log.info("做多止损," + candleNew.getCandleTime() + "," + buyPrice + "," + planLossPrice + "," + loss + "," + loss / buyPrice * 1000);
                                lossTimes++;
                                stopTimes++;
                                sellflag = false;
                                break;
                            }
                            if ((candles15m.getHigh() - buyPrice) >= gainAVG * lossAVG) {
                                sum.add((candles15m.getHigh() - buyPrice) / buyPrice * 1000);
                                log.info("做多止盈," + candleNew.getCandleTime() + "," + buyPrice + "," + planLossPrice + "," + (candles15m.getHigh()-buyPrice) + "," + (candles15m.getHigh()-buyPrice) / buyPrice * 1000);
                                gainTimes++;
                                sellflag = false;
                                break;
                            }
                        }
                        if (sellflag) {
                            Double lastClose = tempList.get(tempList.size() - 1).getClose();
                            double loss = lastClose - buyPrice;
                            if (loss >= 0) {
                                gainTimes++;
                            } else {
                                lossTimes++;
                                stopTimes++;
                            }
                            sum.add(loss / buyPrice * 1000);
                            log.info("做多按时," + candleNew.getCandleTime() + "," + buyPrice + "," + lastClose + "," + loss + "," + loss / buyPrice * 1000);
                        }
                    } else {
                        for (BtcCandles15m candles15m : tempList) {
                            if (candles15m.getHigh() >= planLossPrice) {
                                double loss = buyPrice - planLossPrice;
                                sum.add(loss / buyPrice * 1000);
                                log.info("做空止损," + candleNew.getCandleTime() + "," + buyPrice + "," + planLossPrice + "," + (buyPrice - planLossPrice) + "," + (buyPrice - planLossPrice) / buyPrice * 1000);
                                lossTimes++;
                                stopTimes++;
                                sellflag = false;
                                break;
                            }
                            if ((buyPrice - candles15m.getLow()) >= gainAVG * lossAVG) {
                                sum.add((buyPrice - candles15m.getLow()) / buyPrice * 1000);
                                log.info("做空止盈," + candleNew.getCandleTime() + "," + buyPrice + "," + planLossPrice + "," + (buyPrice - candles15m.getLow()) + "," + (buyPrice - candles15m.getLow()) / buyPrice * 1000);
                                gainTimes++;
                                sellflag = false;
                                break;
                            }
                        }
                        if (sellflag) {
                            Double lastClose = tempList.get(tempList.size() - 1).getClose();
                            double loss = buyPrice - lastClose;
                            if (loss >= 0) {
                                gainTimes++;
                            } else {
                                lossTimes++;
                                stopTimes++;
                            }
                            sum.add(loss / buyPrice * 1000);
                            log.info("做空按时," + candleNew.getCandleTime() + "," + buyPrice + "," + lastClose + "," + loss + "," + loss / buyPrice * 1000);
                        }
                    }
                    //end
                }
                if (flag < 0 && hour1EmaC && hour4EmaC) {
                    //买跌
                    if (stopTimes >= defaultStopTimes) {
                        stopTimes++;
                        if (stopTimes == maxStop) {
                            stopTimes = 0;
                        }
                        continue;
                    }
                    Map<String, Double> doubleMap = getLossPrice(candleNew, buyPrice, flag, lossN, lossM);
                    Double planLossPrice = doubleMap.get("lossPrice");
                    lossAVG = doubleMap.get("lossAVG");
                    //begin
                    String tempStart = DateUtils.addMinutes(candleNew.getCandleTime(), 5);
                    String tempEnd = DateUtils.addMinutes(candleNew.getCandleTime(), handInTime);
                    QueryWrapper<BtcCandles15m> queryWrapper = new QueryWrapper<>();
                    queryWrapper.ge("candle_time", tempStart);
                    queryWrapper.le("candle_time", tempEnd);
                    queryWrapper.orderByAsc("candle_time");
                    List<BtcCandles15m> tempList = btcCandles15mMapper.selectList(queryWrapper);
                    boolean sellflag = true;
                    if (flag > 0) {
                        for (BtcCandles15m candles15m : tempList) {
                            if (candles15m.getLow() <= planLossPrice) {
                                double loss = planLossPrice - buyPrice;
                                sum.add(loss / buyPrice * 1000);
                                log.info("做多止损," + candleNew.getCandleTime() + "," + buyPrice + "," + planLossPrice + "," + loss + "," + loss / buyPrice * 1000);
                                lossTimes++;
                                stopTimes++;
                                sellflag = false;
                                break;
                            }
                            if ((candles15m.getHigh() - buyPrice) >= gainAVG * lossAVG) {
                                sum.add((candles15m.getHigh() - buyPrice) / buyPrice * 1000);
                                log.info("做多止盈," + candleNew.getCandleTime() + "," + buyPrice + "," + planLossPrice + "," + (candles15m.getHigh()-buyPrice) + "," + (candles15m.getHigh()-buyPrice) / buyPrice * 1000);
                                gainTimes++;
                                sellflag = false;
                                break;
                            }
                        }
                        if (sellflag) {
                            Double lastClose = tempList.get(tempList.size() - 1).getClose();
                            double loss = lastClose - buyPrice;
                            if (loss >= 0) {
                                gainTimes++;
                            } else {
                                lossTimes++;
                                stopTimes++;
                            }
                            sum.add(loss / buyPrice * 1000);
                            log.info("做多按时," + candleNew.getCandleTime() + "," + buyPrice + "," + lastClose + "," + loss + "," + loss / buyPrice * 1000);
                        }
                    } else {
                        for (BtcCandles15m candles15m : tempList) {
                            if (candles15m.getHigh() >= planLossPrice) {
                                double loss = buyPrice - planLossPrice;
                                sum.add(loss / buyPrice * 1000);
                                log.info("做空止损," + candleNew.getCandleTime() + "," + buyPrice + "," + planLossPrice + "," + (buyPrice - planLossPrice) + "," + (buyPrice - planLossPrice) / buyPrice * 1000);
                                lossTimes++;
                                stopTimes++;
                                sellflag = false;
                                break;
                            }
                            if ((buyPrice - candles15m.getLow()) >= gainAVG * lossAVG) {
                                sum.add((buyPrice - candles15m.getLow()) / buyPrice * 1000);
                                log.info("做空止盈," + candleNew.getCandleTime() + "," + buyPrice + "," + planLossPrice + "," + (buyPrice - candles15m.getLow()) + "," + (buyPrice - candles15m.getLow()) / buyPrice * 1000);
                                gainTimes++;
                                sellflag = false;
                                break;
                            }
                        }
                        if (sellflag) {
                            Double lastClose = tempList.get(tempList.size() - 1).getClose();
                            double loss = buyPrice - lastClose;
                            if (loss >= 0) {
                                gainTimes++;
                            } else {
                                lossTimes++;
                                stopTimes++;
                            }
                            sum.add(loss / buyPrice * 1000);
                            log.info("做空按时," + candleNew.getCandleTime() + "," + buyPrice + "," + lastClose + "," + loss + "," + loss / buyPrice * 1000);
                        }
                    }
                    //end
                }
            }
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

    private boolean hourEmaCheck(BtcCandles15m candleNew, double flag) {
        //根据当前15m的close，获取当前1h的EMA5 和 EMA10
        String hourEnd = DateUtils.getUTCWithoutMinutes(candleNew.getCandleTime());
        String hourStart = DateUtils.addMinutes(hourEnd, -60);
        BtcCandles1h btcCandles1h = btcCandles1hMapper.selectById(hourStart);
        Double current1hEma5 = CandleUtil.getEMA(Arrays.asList(btcCandles1h.getEma5(), candleNew.getClose()), 5);
        Double current1hEma10 = CandleUtil.getEMA(Arrays.asList(btcCandles1h.getEma10(), candleNew.getClose()), 10);
        if (flag > 0) {
            return (current1hEma5 - current1hEma10) > 0;
        } else {
            return (current1hEma5 - current1hEma10) < 0;
        }
    }

    @SneakyThrows
    private boolean hour4EmaCheck(BtcCandles15m candleNew, double flag) {
        //获取4小时 EMA5和EMA10
        String hour4End = get4HourStart(candleNew.getCandleTime());
        String hour4Start = DateUtils.addMinutes(hour4End, -240);
        BtcCandles4h btcCandles4h = btcCandles4hMapper.selectById(hour4Start);
        Double current4hEma5 = CandleUtil.getEMA(Arrays.asList(btcCandles4h.getEma5(), candleNew.getClose()), 5);
        Double current4hEma10 = CandleUtil.getEMA(Arrays.asList(btcCandles4h.getEma10(), candleNew.getClose()), 10);
        if (flag > 0) {
            return (current4hEma5 - current4hEma10) > 0;
        } else {
            return (current4hEma5 - current4hEma10) < 0;
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

    private static boolean isDifferentLabel(BtcCandles15m one, BtcCandles15m two) {
        Double oneEmaMargin = one.getEma5() - one.getEma10();
        Double twoEmaMargin = two.getEma5() - two.getEma10();
        if (oneEmaMargin * twoEmaMargin > 0) {
            return false;
        } else {
            return true;
        }
    }

    //获取买入价，一般等于下一个5分钟的开盘价获取下一个一分钟的开盘价
    private Double getBuyPrice(String time) {
        String buyTime = DateUtils.addMinutes(time, 15);
        BtcCandles15m btcCandles15m = btcCandles15mMapper.selectById(buyTime);
        return btcCandles15m.getOpen();
    }

    //获取止损价
    private Map<String,Double> getLossPrice(BtcCandles15m candle, Double buyPrice, double flag, Double lossN, Integer lossM) {
        String end = candle.getCandleTime();
        String start = DateUtils.addMinutes(end, -lossM.intValue());
        QueryWrapper<BtcCandles15m> wrapper = new QueryWrapper<>();
        wrapper.ge("candle_time", start);
        wrapper.le("candle_time", end);
        wrapper.orderByAsc("candle_time");
        List<BtcCandles15m> btcCandles15ms = btcCandles15mMapper.selectList(wrapper);
        Double highest = btcCandles15ms.stream().mapToDouble(BtcCandles15m::getHigh).max().getAsDouble();
        Double lowest = btcCandles15ms.stream().mapToDouble(BtcCandles15m::getLow).min().getAsDouble();
        Double highSum = btcCandles15ms.stream().mapToDouble(BtcCandles15m::getHigh).sum();
        Double lowSum = btcCandles15ms.stream().mapToDouble(BtcCandles15m::getLow).sum();
        Double avgMargin = (highSum - lowSum) / btcCandles15ms.size();
        Map<String,Double> map = new HashMap<>();
        //计划损失 1.5N的价格
        map.put("lossAVG",avgMargin);
        Double planLoss = lossN * avgMargin;
        if (flag > 0) {
            map.put("lossPrice", Math.max(buyPrice - planLoss, lowest));
        } else {
            map.put("lossPrice", Math.min(buyPrice + planLoss, highest));
        }
        return map;
    }


    public static Object getRanInArr(Object[] array) {
        int length = array.length;
        int index = (int) (Math.random() * length);
        return array[index];
    }

    public static void printMaxResult() {
        SelectConditions maxKey = SelectConditions.builder().build();
        Double maxValue = 0.0;
        for (Map.Entry<SelectConditions, Double> entry : resultMap.entrySet()) {
            if (entry.getValue() > maxValue) {
                maxValue = entry.getValue();
                maxKey = entry.getKey();
            }
        }
        System.out.println("maxKey: " + maxKey.toString() + " maxValue: " + maxValue);
    }

}