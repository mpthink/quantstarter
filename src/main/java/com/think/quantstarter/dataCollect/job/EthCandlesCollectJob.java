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

import javax.annotation.Resource;
import java.util.List;

/**
 * @author mpthink
 * @date 2020/9/7 19:39
 */
//@Service
@Slf4j
public class EthCandlesCollectJob {

    private static final String instrument_id = "ETH-USD-SWAP";
    private static final Integer DEFAULT_TIME_GAP = 20;

    @Resource
    private ICandlesService candlesService;
    @Resource
    private IEthCandles1mService ethCandles1mService;
    @Resource
    private IEthCandles3mService ethCandles3mService;
    @Resource
    private IEthCandles5mService ethCandles5mService;
    @Resource
    private IEthCandles15mService ethCandles15mService;
    @Resource
    private IEthCandles30mService ethCandles30mService;
    @Resource
    private IEthCandles1hService ethCandles1hService;
    @Resource
    private IEthCandles2hService ethCandles2hService;
    @Resource
    private IEthCandles4hService ethCandles4hService;
    @Resource
    private IEthCandles6hService ethCandles6hService;
    @Resource
    private IEthCandles12hService ethCandles12hService;
    @Resource
    private IEthCandles1dService ethCandles1dService;
    @Resource
    private IEthCandles1wService ethCandles1wService;

    @Scheduled(cron = "30 0/1 * * * ?")
    public void get1MinCandles(){
        getCandles(APIConstants.GRANULARITY1MIN, EthCandles1m.class, ethCandles1mService);
    }

    @Scheduled(cron = "31 0/3 * * * ?")
    public void get3MinCandles(){
        getCandles(APIConstants.GRANULARITY3MIN, EthCandles3m.class, ethCandles3mService);
    }

    @Scheduled(cron = "32 0/5 * * * ?")
    public void get5MinCandles(){
        getCandles(APIConstants.GRANULARITY5MIN, EthCandles5m.class, ethCandles5mService);
    }

    @Scheduled(cron = "33 0/15 * * * ?")
    public void get15MinCandles(){
        getCandles(APIConstants.GRANULARITY15MIN, EthCandles15m.class, ethCandles15mService);
    }

    @Scheduled(cron = "34 0/30 * * * ?")
    public void get30MinCandles(){
        getCandles(APIConstants.GRANULARITY30MIN, EthCandles30m.class, ethCandles30mService);
    }

    @Scheduled(cron = "35 0 0/1 * * ?")
    public void get1HourCandles(){
        getCandles(APIConstants.GRANULARITY1HOUR, EthCandles1h.class, ethCandles1hService);
    }

    @Scheduled(cron = "36 0 0/2 * * ?")
    public void get2HourCandles(){
        getCandles(APIConstants.GRANULARITY2HOUR, EthCandles2h.class, ethCandles2hService);
    }

    @Scheduled(cron = "37 0 0/4 * * ?")
    public void get4HourCandles(){
        getCandles(APIConstants.GRANULARITY4HOUR, EthCandles4h.class, ethCandles4hService);
    }

    @Scheduled(cron = "38 0 0/6 * * ?")
    public void get6HourCandles(){
        getCandles(APIConstants.GRANULARITY6HOUR, EthCandles6h.class, ethCandles6hService);
    }

    @Scheduled(cron = "39 0 0/12 * * ?")
    public void get12HourCandles(){
        getCandles(APIConstants.GRANULARITY12HOUR, EthCandles12h.class, ethCandles12hService);
    }

    @Scheduled(cron = "40 0 0 1/1 * ?")
    public void get1DayCandles(){
        getCandles(APIConstants.GRANULARITY1DAY, EthCandles1d.class, ethCandles1dService);
    }

    @Scheduled(cron = "41 0 0 ? * 1")
    public void get1WeekCandles(){
        getCandles(APIConstants.GRANULARITY1WEEK, EthCandles1w.class, ethCandles1wService);
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
