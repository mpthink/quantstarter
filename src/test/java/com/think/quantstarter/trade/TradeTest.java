package com.think.quantstarter.trade;

import com.think.quantstarter.pilot.service.EthTradeService;
import com.think.quantstarter.rest.bean.swap.result.*;
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
    public void test2(){
        String algo_id = "609902340460941312";
        String dealDetail = ethTradeService.getDealDetail(algo_id);
        System.out.println(dealDetail);
    }

    @Test
    public void test(){
        //下单
        PerOrderResult order1 = ethTradeService.order(FuturesTransactionTypeEnum.OPEN_LONG);
        String order_id = order1.getOrder_id();
        System.out.println("order_id: " + order_id);
        //获取下单价格
        OrderInfo orderInfo = ethTradeService.getOrderInfo(order_id);
        System.out.println("order price: " + Double.valueOf(orderInfo.getPrice_avg()));
        //计划委托
        SwapOrderResultVO order = ethTradeService.swapOrderAlgo(FuturesTransactionTypeEnum.CLOSE_SHORT,"382.9","382.4");
        System.out.println(order);
        String algo_id = order.getData().getAlgo_id();

        //获取委托单信息--测试
        OrderInfo orderInfo2 = ethTradeService.getOrderInfo(algo_id);
        System.out.println("order price: " + orderInfo2);

        //检查
        SwapOrders beforeCancel = ethTradeService.checkAlgoOrder(algo_id);
        System.out.println("check before: " + beforeCancel);
        CancelAlgoOrder cancelOrderAlgo = ethTradeService.cancelOrderAlgo(Arrays.asList(algo_id));
        System.out.println("cancel: " + cancelOrderAlgo);
        SwapOrders afterCancel = ethTradeService.checkAlgoOrder(algo_id);
        System.out.println("check after: " + afterCancel);

    }

}
