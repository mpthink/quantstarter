package com.think.quantstarter;

import com.alibaba.fastjson.JSONArray;
import com.think.quantstarter.rest.config.OkexRestAPIConfig;
import com.think.quantstarter.rest.constant.APIConstants;
import com.think.quantstarter.rest.service.swap.SwapMarketAPIService;
import com.think.quantstarter.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;

@SpringBootTest
class QuantstarterApplicationTests {

    @Resource
    private OkexRestAPIConfig okexRestAPIConfig;

    @Resource
    private SwapMarketAPIService swapMarketAPIService;

    @Test
    void contextLoads() {
        System.out.printf(okexRestAPIConfig.getSecretKey());
    }

    @Test
    void getContractsApi() {
        String contractsApi = swapMarketAPIService.getContractsApi();
        if (contractsApi.startsWith("{")) {
            System.out.println(contractsApi);
        } else {
            System.out.println(contractsApi);
        }
    }

    @Test
    void getKlines(){
        Calendar calendar = Calendar.getInstance();
        Date endTime = calendar.getTime();
        calendar.set(Calendar.MINUTE,(calendar.get(Calendar.MINUTE) - 20));
        Date startDate = calendar.getTime();
        String start = DateUtils.timeToString(startDate,8);
        String end = DateUtils.timeToString(endTime, 8);
        String instrument_id = "BTC-USDT-SWAP";
        String candlesApi = swapMarketAPIService.getHistoryCandlesApi(instrument_id, start, end, APIConstants.GRANULARITY1MIN);
        JSONArray jsonArray = JSONArray.parseArray(candlesApi);
        System.out.println(jsonArray.size());
        System.out.println(jsonArray);

    }
}
