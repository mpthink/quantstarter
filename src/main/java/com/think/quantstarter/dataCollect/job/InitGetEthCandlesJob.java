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
public class InitGetEthCandlesJob {

    private static final String instrument_id = "ETH-USD-SWAP";

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

    private static String START_TIME = "2019-06-06T00:00:00.000Z";
    private static Integer RECORDS_SIZE = 60;

    @PostConstruct
    public void init(){
        initGetETH(APIConstants.GRANULARITY1MIN, EthCandles1m.class, ethCandles1mService);
        initGetETH(APIConstants.GRANULARITY3MIN, EthCandles3m.class, ethCandles3mService);
        initGetETH(APIConstants.GRANULARITY5MIN, EthCandles5m.class, ethCandles5mService);
        initGetETH(APIConstants.GRANULARITY15MIN, EthCandles15m.class, ethCandles15mService);
        initGetETH(APIConstants.GRANULARITY30MIN, EthCandles30m.class, ethCandles30mService);

        initGetETH(APIConstants.GRANULARITY1HOUR, EthCandles1h.class, ethCandles1hService);
        initGetETH(APIConstants.GRANULARITY2HOUR, EthCandles2h.class, ethCandles2hService);
        initGetETH(APIConstants.GRANULARITY4HOUR, EthCandles4h.class, ethCandles4hService);
        initGetETH(APIConstants.GRANULARITY6HOUR, EthCandles6h.class, ethCandles6hService);
        initGetETH(APIConstants.GRANULARITY12HOUR, EthCandles12h.class, ethCandles12hService);

        initGetETH(APIConstants.GRANULARITY1DAY, EthCandles1d.class, ethCandles1dService);

        initGetETH(APIConstants.GRANULARITY1WEEK, EthCandles1w.class, ethCandles1wService);

    }


    @SneakyThrows
    public void initGetETH(String granularity, Class clz, IService service) {
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
