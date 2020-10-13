package com.think.quantstarter.trade;

import com.think.quantstarter.pilot.service.EthTradeService;
import com.think.quantstarter.rest.bean.swap.result.PerOrderResult;
import com.think.quantstarter.rest.enums.FuturesTransactionTypeEnum;
import com.think.quantstarter.rest.service.swap.SwapUserAPIServive;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author mpthink
 * @date 2020/10/13 17:36
 */
@SpringBootTest
public class TradeTest {

    @Resource
    private SwapUserAPIServive swapUserAPIServive;

    @Resource
    private EthTradeService ethTradeService;

    @Test
    public void test(){

        PerOrderResult order = ethTradeService.order(FuturesTransactionTypeEnum.OPEN_SHORT);

        System.out.println(order);

    }

}
