package com.think.quantstarter.analysis;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.think.quantstarter.analysis.utils.CandleUtil;
import com.think.quantstarter.dataCollect.entity.EthCandles1h;
import com.think.quantstarter.dataCollect.entity.EthCandles4h;
import com.think.quantstarter.dataCollect.entity.EthCandles5m;
import com.think.quantstarter.dataCollect.mapper.EthCandles1hMapper;
import com.think.quantstarter.dataCollect.mapper.EthCandles4hMapper;
import com.think.quantstarter.dataCollect.mapper.EthCandles5mMapper;
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
public class EthFindBestParamsTest {

    @Resource
    private EthCandles5mMapper ethCandles5mMapper;
    @Resource
    private EthCandles1hMapper ethCandles1hMapper;
    @Resource
    private EthCandles4hMapper ethCandles4hMapper;

    //需要随机的参数，以数组存放，用一个map来存储参数组合和
    //计划损失N的倍数
    private static Double[] lossNArray = new Double[]{0.5, 1.0, 1.5, 2.0, 2.5, 3.0};
    //最大损失取过去多少分钟的
    private static Integer[] lossMArray = new Integer[]{30, 40, 60, 80, 90, 120};
    //最长持有多久
    private static Integer[] handMArray = new Integer[]{35, 65, 95, 125, 155, 185, 215, 245};
    //条件判断随机
    private static Boolean[] booleanArray = new Boolean[]{true, false};
    //多长时间内不买第二单
    private static Integer[] intervalArray = new Integer[]{0, 30, 60};

    //存储条件和结果
    private static Map<SelectConditions, Double> resultMap = new HashMap<>();

    private static int gainTimes = 0;
    private static int lossTimes = 0;

    @Test
    public void testMaxMap() {
        while (true) {
            double lossN = (double) getRanInArr(lossNArray);
            int lossM = (int) getRanInArr(lossMArray);
            boolean hour1Ema = (boolean) getRanInArr(booleanArray);
            boolean hour4Ema = (boolean) getRanInArr(booleanArray);
            boolean hour1Trend = (boolean) getRanInArr(booleanArray);
            boolean hour4Trend = (boolean) getRanInArr(booleanArray);
            int handInTime = (int) getRanInArr(handMArray);
            int intervalBuy = (int) getRanInArr(intervalArray);
            SelectConditions selectConditions = SelectConditions.builder()
                    .lossN(lossN).lossM(lossM)
                    .hour1Ema(hour1Ema).hour4Ema(hour4Ema)
                    .hour1Trend(hour1Trend).hour4Trend(hour4Trend)
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
                .hour1Trend(false).hour4Trend(false)
                .handInTime(185).intervalBuy(120).build();
        countStrategy(selectConditions);
    }


    @SneakyThrows
    @Test
    public void findMax() {
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executorService = new ThreadPoolExecutor(cores, cores, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        //countStrategy(1.0, 40, true, true, false, false,125,0);
        int expectedSize = 36853;
        while (true) {
            double lossN = (double) getRanInArr(lossNArray);
            int lossM = (int) getRanInArr(lossMArray);
            boolean hour1Ema = (boolean) getRanInArr(booleanArray);
            boolean hour4Ema = (boolean) getRanInArr(booleanArray);
            boolean hour1Trend = (boolean) getRanInArr(booleanArray);
            boolean hour4Trend = (boolean) getRanInArr(booleanArray);
            int handInTime = (int) getRanInArr(handMArray);
            int intervalBuy = (int) getRanInArr(intervalArray);
            SelectConditions selectConditions = SelectConditions.builder()
                    .lossN(lossN).lossM(lossM)
                    .hour1Ema(hour1Ema).hour4Ema(hour4Ema)
                    .hour1Trend(hour1Trend).hour4Trend(hour4Trend)
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
        double lossN = condition.getLossN();
        int lossM = condition.getLossM();
        boolean hour1Ema = condition.isHour1Ema();
        boolean hour4Ema = condition.isHour4Ema();
        boolean hour1Trend = condition.isHour1Trend();
        boolean hour4Trend = condition.isHour4Trend();
        int handInTime = condition.getHandInTime();
        int intervalBuy = condition.getIntervalBuy();

        QueryWrapper<EthCandles5m> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("candle_time");
        wrapper.last("limit 1");
        EthCandles5m oldest = ethCandles5mMapper.selectOne(wrapper);
        String start = oldest.getCandleTime();
        String end = start;
        String lastBuyTime = start;
        int i = 0;
        List<Double> sum = new ArrayList<>();
        while (DateUtils.parseUTCTime(end).before(new Date())) {
            end = DateUtils.addMinutes(start, 5);
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
            List<EthCandles5m> ethCandles5ms = ethCandles5mMapper.selectList(wrapper);
            if (ethCandles5ms.size() != 2) {
                //System.out.println("No enough data for analysis!");
                log.info("Finish............" + condition + ",sum: " + sum.stream().mapToDouble(Double::doubleValue).sum()+","+sum.size()+","+gainTimes+","+lossTimes);
                //准备写入map中
                resultMap.put(condition, sum.stream().mapToDouble(Double::doubleValue).sum());
                return;
            }
            EthCandles5m candleOld = ethCandles5ms.get(0);
            EthCandles5m candleNew = ethCandles5ms.get(1);
            //判断前一个5m的ema5-ema10和当前ema5-ema10值是否相反
            if (isDifferentLabel(candleOld, candleNew)) {
                Double minuteEma5 = candleNew.getEma5();
                Double minuteEma10 = candleNew.getEma10();
                double flag = minuteEma5 - minuteEma10;
                boolean hour1EmaC = hour1Ema ? hourEmaCheck(candleNew, flag) : true;
                boolean hour4EmaC = hour4Ema ? hour4EmaCheck(candleNew, flag) : true;
                boolean hour1TrendC = hour1Trend ? hourTrend(candleNew, flag) : true;
                boolean hour4TrendC = hour4Trend ? hour4Trend(candleNew, flag) : true;

                Double buyPrice = getBuyPrice(candleNew.getCandleTime());


                if (flag > 0 && hour1EmaC && hour4EmaC && hour1TrendC && hour4TrendC) {
                    //判断是否在120分钟内多次买入
                    if (countTimeGapMinutes(candleNew.getCandleTime(), lastBuyTime) < intervalBuy) {
                        continue;
                    } else {
                        lastBuyTime = candleNew.getCandleTime();
                    }
                    //买涨
                    Double planLossPrice = getLossPrice(candleNew, buyPrice, flag, lossN, lossM);
                    sellAndRecord(candleNew.getCandleTime(), buyPrice, planLossPrice, flag, handInTime, sum);
                }
                if (flag < 0 && hour1EmaC && hour4EmaC && hour1TrendC && hour4TrendC) {
                    //判断是否在120分钟内多次买入
                    if (countTimeGapMinutes(candleNew.getCandleTime(), lastBuyTime) < intervalBuy) {
                        continue;
                    } else {
                        lastBuyTime = candleNew.getCandleTime();
                    }
                    //买跌
                    Double planLossPrice = getLossPrice(candleNew, buyPrice, flag, lossN, lossM);
                    sellAndRecord(candleNew.getCandleTime(), buyPrice, planLossPrice, flag, handInTime, sum);
                }
            }
        }
    }

    private void sellAndRecord(String time, double buyPrice, double planLossPrice, double flag, int handInTime,
                               List<Double> sum) {
        //由于1分钟数据有限，不能获取当前1分钟的价格，按照接下来5分钟的走势，按1个小时定时止损来看看是否会止损或者退出
        String tempStart = DateUtils.addMinutes(time, 5);
        String tempEnd = DateUtils.addMinutes(time, handInTime);
        QueryWrapper<EthCandles5m> wrapper = new QueryWrapper<>();
        wrapper.ge("candle_time", tempStart);
        wrapper.le("candle_time", tempEnd);
        wrapper.orderByAsc("candle_time");
        List<EthCandles5m> tempList = ethCandles5mMapper.selectList(wrapper);
        boolean sellflag = true;
        if (flag > 0) {
            for (EthCandles5m candles5m : tempList) {
                if (candles5m.getLow() <= planLossPrice) {
                    double loss = planLossPrice - buyPrice;
                    sum.add(loss / buyPrice * 1000);
                    log.info("做多止损," + time + "," + buyPrice + "," + planLossPrice + "," + loss + "," + loss / buyPrice * 1000);
                    lossTimes++;
                    sellflag = false;
                    break;
                }
            }
            if (sellflag) {
                Double lastClose = tempList.get(tempList.size() - 1).getClose();
                double loss = lastClose - buyPrice;
                if(loss>=0){
                    gainTimes++;
                }else{
                    lossTimes++;
                }
                sum.add(loss / buyPrice * 1000);
                log.info("做多按时," + time + "," + buyPrice + "," + lastClose + "," + loss + "," + loss / buyPrice * 1000);
            }
        } else {
            for (EthCandles5m candles5m : tempList) {
                if (candles5m.getHigh() >= planLossPrice) {
                    double loss = buyPrice - planLossPrice;
                    sum.add(loss / buyPrice * 1000);
                    log.info("做空止损," + time + "," + buyPrice + "," + planLossPrice + "," + (buyPrice - planLossPrice) + "," + (buyPrice - planLossPrice) / buyPrice * 1000);
                    lossTimes++;
                    sellflag = false;
                    break;
                }
            }
            if (sellflag) {
                Double lastClose = tempList.get(tempList.size() - 1).getClose();
                double loss = buyPrice - lastClose;
                if(loss>=0){
                    gainTimes++;
                }else{
                    lossTimes++;
                }
                sum.add(loss / buyPrice * 1000);
                log.info("做空按时," + time + "," + buyPrice + "," + lastClose + "," + loss + "," + loss / buyPrice * 1000);
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

    private boolean hourEmaCheck(EthCandles5m candleNew, double flag) {
        //根据当前5m的close，获取当前1h的EMA5 和 EMA10
        String hourEnd = DateUtils.getUTCWithoutMinutes(candleNew.getCandleTime());
        String hourStart = DateUtils.addMinutes(hourEnd, -60);
        EthCandles1h ethCandles1h = ethCandles1hMapper.selectById(hourStart);
        Double current1hEma5 = CandleUtil.getEMA(Arrays.asList(ethCandles1h.getEma5(), candleNew.getClose()), 5);
        Double current1hEma10 = CandleUtil.getEMA(Arrays.asList(ethCandles1h.getEma10(), candleNew.getClose()), 10);
        if (flag > 0) {
            return (current1hEma5 - current1hEma10) > 0;
        } else {
            return (current1hEma5 - current1hEma10) < 0;
        }
    }

    @SneakyThrows
    private boolean hour4EmaCheck(EthCandles5m candleNew, double flag) {
        //获取4小时 EMA5和EMA10
        String hour4End = get4HourStart(candleNew.getCandleTime());
        String hour4Start = DateUtils.addMinutes(hour4End, -240);
        EthCandles4h ethCandles4h = ethCandles4hMapper.selectById(hour4Start);
        Double current4hEma5 = CandleUtil.getEMA(Arrays.asList(ethCandles4h.getEma5(), candleNew.getClose()), 5);
        Double current4hEma10 = CandleUtil.getEMA(Arrays.asList(ethCandles4h.getEma10(), candleNew.getClose()), 10);
        if (flag > 0) {
            return (current4hEma5 - current4hEma10) > 0;
        } else {
            return (current4hEma5 - current4hEma10) < 0;
        }
    }

    private boolean hourTrend(EthCandles5m candleNew, double flag) {
        QueryWrapper<EthCandles1h> wrapper = new QueryWrapper<>();
        wrapper.le("candle_time", candleNew.getCandleTime());
        wrapper.orderByDesc("candle_time");
        wrapper.last("limit 2");
        List<EthCandles1h> ethCandles4hs = ethCandles1hMapper.selectList(wrapper);
        EthCandles1h second = ethCandles4hs.get(1);
        Double current4hEma5 = CandleUtil.getEMA(Arrays.asList(second.getEma5(), candleNew.getClose()), 5);
        Double current4hEma10 = CandleUtil.getEMA(Arrays.asList(second.getEma10(), candleNew.getClose()), 10);
        if (flag > 0) {
            Double secondGap = second.getEma5() - second.getEma10();
            Double firstGap = current4hEma5 - current4hEma10;
            if ((secondGap - firstGap) > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            Double secondGap = second.getEma10() - second.getEma5();
            Double firstGap = current4hEma10 - current4hEma5;
            if ((secondGap - firstGap) > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    private boolean hour4Trend(EthCandles5m candleNew, double flag) {
        QueryWrapper<EthCandles4h> wrapper = new QueryWrapper<>();
        wrapper.le("candle_time", candleNew.getCandleTime());
        wrapper.orderByDesc("candle_time");
        wrapper.last("limit 2");
        List<EthCandles4h> ethCandles4hs = ethCandles4hMapper.selectList(wrapper);
        EthCandles4h second = ethCandles4hs.get(1);
        Double current4hEma5 = CandleUtil.getEMA(Arrays.asList(second.getEma5(), candleNew.getClose()), 5);
        Double current4hEma10 = CandleUtil.getEMA(Arrays.asList(second.getEma10(), candleNew.getClose()), 10);
        if (flag > 0) {
            Double secondGap = second.getEma5() - second.getEma10();
            Double firstGap = current4hEma5 - current4hEma10;
            if ((secondGap - firstGap) > 0) {
                return true;
            } else {
                return false;
            }
        } else {
            Double secondGap = second.getEma10() - second.getEma5();
            Double firstGap = current4hEma10 - current4hEma5;
            if ((secondGap - firstGap) > 0) {
                return true;
            } else {
                return false;
            }
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

    private static boolean isDifferentLabel(EthCandles5m one, EthCandles5m two) {
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
        String buyTime = DateUtils.addMinutes(time, 5);
        EthCandles5m ethCandles5m = ethCandles5mMapper.selectById(buyTime);
        return ethCandles5m.getOpen();
    }

    //获取止损价
    private Double getLossPrice(EthCandles5m candle, Double buyPrice, double flag, Double lossN, Integer lossM) {
        String end = candle.getCandleTime();
        String start = DateUtils.addMinutes(end, -lossM.intValue());
        QueryWrapper<EthCandles5m> wrapper = new QueryWrapper<>();
        wrapper.ge("candle_time", start);
        wrapper.le("candle_time", end);
        wrapper.orderByAsc("candle_time");
        List<EthCandles5m> ethCandles5ms = ethCandles5mMapper.selectList(wrapper);
        Double highest = ethCandles5ms.stream().mapToDouble(EthCandles5m::getHigh).max().getAsDouble();
        Double lowest = ethCandles5ms.stream().mapToDouble(EthCandles5m::getLow).min().getAsDouble();
        Double highSum = ethCandles5ms.stream().mapToDouble(EthCandles5m::getHigh).sum();
        Double lowSum = ethCandles5ms.stream().mapToDouble(EthCandles5m::getLow).sum();
        Double avgMargin = (highSum - lowSum) / ethCandles5ms.size();
        //计划损失 1.5N的价格
        Double planLoss = lossN * avgMargin;
        if (flag > 0) {
            return Math.max(buyPrice - planLoss, lowest);
        } else {
            return Math.min(buyPrice + planLoss, highest);
        }
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
