package com.think.quantstarter.dataCollect.job;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.think.quantstarter.dataCollect.entity.*;
import com.think.quantstarter.dataCollect.manager.ICandlesService;
import com.think.quantstarter.dataCollect.service.*;
import com.think.quantstarter.dataCollect.utils.ConvertToObjectUtil;
import com.think.quantstarter.rest.constant.APIConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author mpthink
 * @date 2020/9/7 19:39
 */
@Service
@Slf4j
public class BtcCandlesCollectJob {

    private static final String instrument_id = "BTC-USD-SWAP";
    private static final Integer DEFAULT_TIME_GAP = 20;

    @Resource
    private ICandlesService candlesService;
    @Resource
    private IBtcCandles1mService btcCandles1mService;
    @Resource
    private IBtcCandles3mService btcCandles3mService;
    @Resource
    private IBtcCandles5mService btcCandles5mService;
    @Resource
    private IBtcCandles15mService btcCandles15mService;
    @Resource
    private IBtcCandles30mService btcCandles30mService;
    @Resource
    private IBtcCandles1hService btcCandles1hService;
    @Resource
    private IBtcCandles2hService btcCandles2hService;
    @Resource
    private IBtcCandles4hService btcCandles4hService;
    @Resource
    private IBtcCandles6hService btcCandles6hService;
    @Resource
    private IBtcCandles12hService btcCandles12hService;
    @Resource
    private IBtcCandles1dService btcCandles1dService;
    @Resource
    private IBtcCandles1wService btcCandles1wService;

    @Scheduled(cron = "10 0/1 * * * ?")
    public void get1MinCandles(){
        getCandles(APIConstants.GRANULARITY1MIN, BtcCandles1m.class, btcCandles1mService);
    }

    public void get3MinCandles(){
        getCandles(APIConstants.GRANULARITY3MIN, BtcCandles3m.class, btcCandles3mService);
    }

    public void get5MinCandles(){
        getCandles(APIConstants.GRANULARITY5MIN, BtcCandles5m.class, btcCandles5mService);
    }

    public void get15MinCandles(){
        getCandles(APIConstants.GRANULARITY15MIN, BtcCandles15m.class, btcCandles15mService);
    }

    public void get30MinCandles(){
        getCandles(APIConstants.GRANULARITY30MIN, BtcCandles30m.class, btcCandles30mService);
    }

    public void get1HourCandles(){
        getCandles(APIConstants.GRANULARITY1HOUR, BtcCandles1h.class, btcCandles1hService);
    }

    public void get2HourCandles(){
        getCandles(APIConstants.GRANULARITY2HOUR, BtcCandles2h.class, btcCandles2hService);
    }

    public void get4HourCandles(){
        getCandles(APIConstants.GRANULARITY4HOUR, BtcCandles4h.class, btcCandles4hService);
    }

    public void get6HourCandles(){
        getCandles(APIConstants.GRANULARITY6HOUR, BtcCandles6h.class, btcCandles6hService);
    }

    public void get12HourCandles(){
        getCandles(APIConstants.GRANULARITY12HOUR, BtcCandles12h.class, btcCandles12hService);
    }

    public void get1DayCandles(){
        getCandles(APIConstants.GRANULARITY1DAY, BtcCandles1d.class, btcCandles1dService);
    }

    public void get1WeekCandles(){
        getCandles(APIConstants.GRANULARITY1WEEK, BtcCandles1w.class, btcCandles1wService);
    }

    private void getCandles(String granularity, Class clz, IService service){
        JSONArray candles = candlesService.getCandles(instrument_id, granularity, DEFAULT_TIME_GAP);
        List<Object> objectList = ConvertToObjectUtil.convertJsonArrayToObjects(candles, clz);
        service.saveOrUpdateBatch(objectList);
    }

    private void getCandles(String granularity, Class clz, IService service, Integer customGap){
        JSONArray candles = candlesService.getCandles(instrument_id, granularity, customGap);
        List<Object> objectList = ConvertToObjectUtil.convertJsonArrayToObjects(candles, clz);
        service.saveOrUpdateBatch(objectList);
    }

}
