package com.think.quantstarter.dataCollect.job;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.think.quantstarter.dataCollect.entity.EthCandles1m;
import com.think.quantstarter.dataCollect.manager.ICandlesService;
import com.think.quantstarter.dataCollect.service.IEthCandles1mService;
import com.think.quantstarter.dataCollect.utils.ConvertToObjectUtil;
import com.think.quantstarter.rest.constant.APIConstants;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author mpthink
 * @date 2020/10/22 10:04
 */
//@Service
@Slf4j
public class JobTest {
    private static final String instrument_id = "ETH-USDT-SWAP";
    private static final Integer DEFAULT_TIME_GAP = 20;
    @Resource
    private ICandlesService candlesService;
    @Resource
    private IEthCandles1mService ethCandles1mService;
    //@Scheduled(cron = "1 0/1 * * * ?")
    public void get1MinCandles(){
        getCandles(APIConstants.GRANULARITY1MIN, EthCandles1m.class, ethCandles1mService);
    }

    private void getCandles(String granularity, Class clz, IService service){
        JSONArray candles = candlesService.getCandles(instrument_id, granularity, 2);
        List<Object> objectList = ConvertToObjectUtil.convertJsonArrayToObjects(candles, clz);
        log.info("Data: [{}]", objectList);
    }

}
