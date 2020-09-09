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
public class BchCandlesCollectJob {

    private static final String instrument_id = "BCH-USD-SWAP";
    private static final Integer DEFAULT_TIME_GAP = 20;

    @Resource
    private ICandlesService candlesService;
    @Resource
    private IBchCandles1mService bchCandles1mService;
    @Resource
    private IBchCandles3mService bchCandles3mService;
    @Resource
    private IBchCandles5mService bchCandles5mService;
    @Resource
    private IBchCandles15mService bchCandles15mService;
    @Resource
    private IBchCandles30mService bchCandles30mService;
    @Resource
    private IBchCandles1hService bchCandles1hService;
    @Resource
    private IBchCandles2hService bchCandles2hService;
    @Resource
    private IBchCandles4hService bchCandles4hService;
    @Resource
    private IBchCandles6hService bchCandles6hService;
    @Resource
    private IBchCandles12hService bchCandles12hService;
    @Resource
    private IBchCandles1dService bchCandles1dService;
    @Resource
    private IBchCandles1wService bchCandles1wService;

    @Scheduled(cron = "11 0/1 * * * ?")
    public void get1MinCandles(){
        getCandles(APIConstants.GRANULARITY1MIN, BchCandles1m.class, bchCandles1mService);
    }

    @Scheduled(cron = "13 0/3 * * * ?")
    public void get3MinCandles(){
        getCandles(APIConstants.GRANULARITY3MIN, BchCandles3m.class, bchCandles3mService);
    }

    @Scheduled(cron = "15 0/5 * * * ?")
    public void get5MinCandles(){
        getCandles(APIConstants.GRANULARITY5MIN, BchCandles5m.class, bchCandles5mService);
    }

    @Scheduled(cron = "17 0/15 * * * ?")
    public void get15MinCandles(){
        getCandles(APIConstants.GRANULARITY15MIN, BchCandles15m.class, bchCandles15mService);
    }

    @Scheduled(cron = "19 0/30 * * * ?")
    public void get30MinCandles(){
        getCandles(APIConstants.GRANULARITY30MIN, BchCandles30m.class, bchCandles30mService);
    }

    @Scheduled(cron = "21 0 0/1 * * ?")
    public void get1HourCandles(){
        getCandles(APIConstants.GRANULARITY1HOUR, BchCandles1h.class, bchCandles1hService);
    }

    @Scheduled(cron = "23 0 0/2 * * ?")
    public void get2HourCandles(){
        getCandles(APIConstants.GRANULARITY2HOUR, BchCandles2h.class, bchCandles2hService);
    }

    @Scheduled(cron = "25 0 0/4 * * ?")
    public void get4HourCandles(){
        getCandles(APIConstants.GRANULARITY4HOUR, BchCandles4h.class, bchCandles4hService);
    }

    @Scheduled(cron = "27 0 0/6 * * ?")
    public void get6HourCandles(){
        getCandles(APIConstants.GRANULARITY6HOUR, BchCandles6h.class, bchCandles6hService);
    }

    @Scheduled(cron = "30 0 0/12 * * ?")
    public void get12HourCandles(){
        getCandles(APIConstants.GRANULARITY12HOUR, BchCandles12h.class, bchCandles12hService);
    }

    @Scheduled(cron = "32 0 0 1/1 * ?")
    public void get1DayCandles(){
        getCandles(APIConstants.GRANULARITY1DAY, BchCandles1d.class, bchCandles1dService);
    }

    @Scheduled(cron = "34 0 0 ? * 1")
    public void get1WeekCandles(){
        getCandles(APIConstants.GRANULARITY1WEEK, BchCandles1w.class, bchCandles1wService);
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
