package com.think.quantstarter.dataCollect.manager.impl;

import com.alibaba.fastjson.JSONArray;
import com.think.quantstarter.dataCollect.manager.ICandlesService;
import com.think.quantstarter.rest.service.swap.SwapMarketAPIService;
import com.think.quantstarter.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

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

    @Override
    public JSONArray getCandles(String instrument_id, String granularity, Integer timeGap){
        Calendar calendar = Calendar.getInstance();
        Date endTime = calendar.getTime();
        calendar.add(Calendar.MINUTE, (calendar.get(Calendar.MINUTE) - timeGap * Integer.valueOf(granularity) / 60));
        Date startDate = calendar.getTime();
        String start = DateUtils.timeToString(startDate,8);
        String end = DateUtils.timeToString(endTime, 8);
        String candlesApi = swapMarketAPIService.getCandlesApi(instrument_id, start, end, granularity);
        JSONArray jsonArray = JSONArray.parseArray(candlesApi);
        return jsonArray;
    }

}
