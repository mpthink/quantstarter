package com.think.quantstarter.dataCollect.job;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.think.quantstarter.dataCollect.entity.*;
import com.think.quantstarter.dataCollect.manager.ICandlesService;
import com.think.quantstarter.dataCollect.service.*;
import com.think.quantstarter.dataCollect.utils.ConvertToObjectUtil;
import com.think.quantstarter.rest.constant.APIConstants;
import com.think.quantstarter.utils.DateUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author mpthink
 * @date 2020/9/7 22:29
 */
//@Service
@Slf4j
public class InitGetBtcCandlesJob {

    private static final String instrument_id = "BTC-USD-SWAP";

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

    private static String START_TIME = "2019-06-06T00:00:00.000Z";
    private static Integer RECORDS_SIZE = 60;

    @PostConstruct
    public void init(){
        initGetBTC(APIConstants.GRANULARITY1MIN, BtcCandles1m.class, btcCandles1mService);
        initGetBTC(APIConstants.GRANULARITY3MIN, BtcCandles3m.class, btcCandles3mService);
        initGetBTC(APIConstants.GRANULARITY5MIN, BtcCandles5m.class, btcCandles5mService);
        initGetBTC(APIConstants.GRANULARITY15MIN, BtcCandles15m.class, btcCandles15mService);
        initGetBTC(APIConstants.GRANULARITY30MIN, BtcCandles30m.class, btcCandles30mService);

        initGetBTC(APIConstants.GRANULARITY1HOUR, BtcCandles1h.class, btcCandles1hService);
        initGetBTC(APIConstants.GRANULARITY2HOUR, BtcCandles2h.class, btcCandles2hService);
        initGetBTC(APIConstants.GRANULARITY4HOUR, BtcCandles4h.class, btcCandles4hService);
        initGetBTC(APIConstants.GRANULARITY6HOUR, BtcCandles6h.class, btcCandles6hService);
        initGetBTC(APIConstants.GRANULARITY12HOUR, BtcCandles12h.class, btcCandles12hService);

        initGetBTC(APIConstants.GRANULARITY1DAY, BtcCandles1d.class, btcCandles1dService);

        initGetBTC(APIConstants.GRANULARITY1WEEK, BtcCandles1w.class, btcCandles1wService);
    }


    @SneakyThrows
    public void initGetBTC(String granularity, Class clz, IService service) {
        Date startTime = DateUtils.parseUTCTime(START_TIME);
        while (startTime.before(new Date())){
            Thread.sleep(200);
            Date endTime = getEndTime(startTime, granularity);
            JSONArray candles = candlesService.getCandles(DateUtils.timeToString(startTime, 8), DateUtils.timeToString(endTime, 8), instrument_id, granularity);
            if(candles == null || candles.size() == 0){
                startTime = endTime;
            }else{
                List<Object> objectList = ConvertToObjectUtil.convertJsonArrayToObjects(candles, clz);
                service.saveOrUpdateBatch(objectList);
                startTime = endTime;
            }
        }
    }

    private static Date getEndTime(Date startTime, String granularity){
        Integer candles = Integer.valueOf(granularity);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.add(Calendar.MINUTE, RECORDS_SIZE * candles / 60);
        return calendar.getTime();
    }

    public static void main(String[] args) throws ParseException {
        String START_TIME = "2018-12-06T00:00:00.000Z";
        Date startTime = DateUtils.parseUTCTime(START_TIME);
        Date endTime = getEndTime(startTime, APIConstants.GRANULARITY1DAY);
        System.out.println(endTime);
    }

}
