package com.think.quantstarter.trade;

import com.think.quantstarter.pilot.service.EthTradeService;
import com.think.quantstarter.rest.bean.swap.result.CancelAlgoOrder;
import com.think.quantstarter.rest.bean.swap.result.SwapOrderResultVO;
import com.think.quantstarter.rest.bean.swap.result.SwapOrders;
import com.think.quantstarter.rest.enums.FuturesTransactionTypeEnum;
import com.think.quantstarter.rest.service.swap.SwapUserAPIServive;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;

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

        SwapOrderResultVO order = ethTradeService.swapOrderAlgo(FuturesTransactionTypeEnum.CLOSE_SHORT,"380","387");
        System.out.println(order);
        String algo_id = order.getData().getAlgo_id();
        //check
        SwapOrders beforeCancel = ethTradeService.checkAlgoOrder(algo_id);
        System.out.println("check before: " + beforeCancel);
        CancelAlgoOrder cancelOrderAlgo = ethTradeService.cancelOrderAlgo(Arrays.asList(algo_id));
        System.out.println("cancel: " + cancelOrderAlgo);
        SwapOrders afterCancel = ethTradeService.checkAlgoOrder(algo_id);
        System.out.println("check after: " + afterCancel);

    }

}
