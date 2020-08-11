package com.think.quantstarter.service;

import com.alibaba.fastjson.JSONArray;
import com.think.quantstarter.config.OkexConfig;
import com.think.quantstarter.rest.constant.APIConstants;
import com.think.quantstarter.rest.service.swap.SwapMarketAPIService;
import com.think.quantstarter.utils.DateUtils;
import com.think.quantstarter.websocket.WebSocketClient;
import com.think.quantstarter.websocket.constant.ChannelConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

/**
 * @author mpthink
 * @date 2020/8/11 15:03
 */
@Service
@Slf4j
public class DataCollectService {

    @Resource
    private SwapMarketAPIService swapMarketAPIService;
    private static final String instrument_id = "BTC-USD-SWAP";

    @Resource
    private OkexConfig okexConfig;

    @PostConstruct
    public void subscribeWSS() {
        WebSocketClient webSocketClient = new WebSocketClient();
        webSocketClient.connection(okexConfig.getWssUrl());
        webSocketClient.subscribe(ChannelConstants.subscribes);
        get3MinKline();
        get5MinKline();
        get15MinKline();
        get30MinKline();
        get1HourKline();
        get4HourKline();
        get1DayKline();
        get1WeekKline();
    }

    @Scheduled(cron = "1 0/1 * * * ?")
    private void insertToDB(){
        JSONArray klines = getKlines(APIConstants.GRANULARITY3MIN);
        CacheKlineData.CANDLE3MIN.clear();
        if(klines != null && klines.size() != 0){
            CacheKlineData.CANDLE3MIN = klines;
        }
    }

    @Scheduled(cron = "30 0/3 * * * ?")
    private void get3MinKline(){
        JSONArray klines = getKlines(APIConstants.GRANULARITY3MIN);
        CacheKlineData.CANDLE3MIN.clear();
        if(klines != null && klines.size() != 0){
            CacheKlineData.CANDLE3MIN = klines;
        }
    }

    @Scheduled(cron = "30 0/5 * * * ?")
    private void get5MinKline(){
        JSONArray klines = getKlines(APIConstants.GRANULARITY5MIN);
        if(klines != null && klines.size() != 0){
            CacheKlineData.CANDLE5MIN = klines;
        }
    }

    @Scheduled(cron = "30 0/15 * * * ?")
    private void get15MinKline(){
        JSONArray klines = getKlines(APIConstants.GRANULARITY15MIN);
        if(klines != null && klines.size() != 0){
            CacheKlineData.CANDLE15MIN = klines;
        }
    }

    @Scheduled(cron = "30 0/30 * * * ?")
    private void get30MinKline(){
        JSONArray klines = getKlines(APIConstants.GRANULARITY30MIN);
        if(klines != null && klines.size() != 0){
            CacheKlineData.CANDLE30MIN = klines;
        }
    }

    @Scheduled(cron = "30 0 0/1 * * ?")
    private void get1HourKline(){
        JSONArray klines = getKlines(APIConstants.GRANULARITY1HOUR);
        if(klines != null && klines.size() != 0){
            CacheKlineData.CANDLE1HOUR = klines;
        }
    }

    @Scheduled(cron = "30 0 0/4 * * ?")
    private void get4HourKline(){
        JSONArray klines = getKlines(APIConstants.GRANULARITY4HOUR);
        if(klines != null && klines.size() != 0){
            CacheKlineData.CANDLE4HOUR = klines;
        }
    }

    @Scheduled(cron = "30 0 0 1/1 * ?")
    private void get1DayKline(){
        JSONArray klines = getKlines(APIConstants.GRANULARITY1DAY);
        if(klines != null && klines.size() != 0){
            CacheKlineData.CANDLE1DAY = klines;
        }
    }

    @Scheduled(cron = "30 0 0 ? * 1")
    private void get1WeekKline(){
        JSONArray klines = getKlines(APIConstants.GRANULARITY1WEEK);
        if(klines != null && klines.size() != 0){
            CacheKlineData.CANDLE1WEEK = klines;
        }
    }


    /**
     * Note: 最近一次时间的数据在websocket获取后要刷新
     *
     * @param granularity
     * @return
     */
    @SneakyThrows
    public JSONArray getKlines(String granularity) {
        Integer candles = Integer.valueOf(granularity);
        int failedNum = 0;
        while (true) {
            if(failedNum>APIConstants.FAILED_TIMES_GETKLINES){
                log.error("Failed times exceed the max limit: {}", APIConstants.FAILED_TIMES_GETKLINES);
                break;
            }
            Calendar calendar = Calendar.getInstance();
            Date endTime = calendar.getTime();
            calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE) - APIConstants.KLINES_NUMBERS * candles / 60));
            Date startDate = calendar.getTime();
            String start = DateUtils.timeToString(startDate,8);
            String end = DateUtils.timeToString(endTime, 8);
            try{
                String candlesApi = swapMarketAPIService.getCandlesApi(instrument_id, start, end, APIConstants.GRANULARITY1MIN);
                JSONArray jsonArray = JSONArray.parseArray(candlesApi);
                if(jsonArray.size() == APIConstants.KLINES_NUMBERS){
                    return jsonArray;
                }else{
                    failedNum++;
                    continue;
                }
            }catch (Exception e){
                log.error("Get kline data failed, error message: {}", e.getMessage());
                failedNum++;
                Thread.sleep(100);
            }
        }
        return null;
    }
}
