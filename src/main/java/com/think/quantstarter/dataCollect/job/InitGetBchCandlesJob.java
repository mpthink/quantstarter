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
import org.springframework.stereotype.Service;

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
@Service
@Slf4j
public class InitGetBchCandlesJob {

    private static final String instrument_id = "BCH-USD-SWAP";

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

    private static String START_TIME = "2019-06-06T00:00:00.000Z";
    private static Integer RECORDS_SIZE = 60;

    @PostConstruct
    public void init(){
        initGetBCH(APIConstants.GRANULARITY1MIN, BchCandles1m.class, bchCandles1mService);
        initGetBCH(APIConstants.GRANULARITY3MIN, BchCandles3m.class, bchCandles3mService);
        initGetBCH(APIConstants.GRANULARITY5MIN, BchCandles5m.class, bchCandles5mService);
        initGetBCH(APIConstants.GRANULARITY15MIN, BchCandles15m.class, bchCandles15mService);
        initGetBCH(APIConstants.GRANULARITY30MIN, BchCandles30m.class, bchCandles30mService);

        initGetBCH(APIConstants.GRANULARITY1HOUR, BchCandles1h.class, bchCandles1hService);
        initGetBCH(APIConstants.GRANULARITY2HOUR, BchCandles2h.class, bchCandles2hService);
        initGetBCH(APIConstants.GRANULARITY4HOUR, BchCandles4h.class, bchCandles4hService);
        initGetBCH(APIConstants.GRANULARITY6HOUR, BchCandles6h.class, bchCandles6hService);
        initGetBCH(APIConstants.GRANULARITY12HOUR, BchCandles12h.class, bchCandles12hService);

        initGetBCH(APIConstants.GRANULARITY1DAY, BchCandles1d.class, bchCandles1dService);

        initGetBCH(APIConstants.GRANULARITY1WEEK, BchCandles1w.class, bchCandles1wService);

    }


    @SneakyThrows
    public void initGetBCH(String granularity, Class clz, IService service) {
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
