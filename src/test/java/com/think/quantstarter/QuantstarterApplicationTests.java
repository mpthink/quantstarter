package com.think.quantstarter;

import com.think.quantstarter.rest.config.OkexRestAPIConfig;
import com.think.quantstarter.rest.service.swap.SwapMarketAPIService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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
}
