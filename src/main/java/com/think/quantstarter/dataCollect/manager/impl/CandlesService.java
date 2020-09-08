package com.think.quantstarter.dataCollect.manager.impl;

import com.alibaba.fastjson.JSONArray;
import com.think.quantstarter.dataCollect.manager.ICandlesService;
import com.think.quantstarter.rest.service.swap.SwapMarketAPIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author mpthink
 * @date 2020/9/7 22:00
 */
@Service
@Slf4j
public class CandlesService implements ICandlesService {

    @Resource
    private SwapMarketAPIService swapMarketAPIService;

    @Override
    public JSONArray getCandles(String start,String end, String instrument_id, String granularity){
        String candlesApi = swapMarketAPIService.getCandlesApi(instrument_id, start, end, granularity);
        JSONArray jsonArray = JSONArray.parseArray(candlesApi);
        return jsonArray;
    }

}
