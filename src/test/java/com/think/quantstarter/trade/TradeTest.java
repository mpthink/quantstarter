package com.think.quantstarter.trade;

import com.alibaba.fastjson.JSON;
import com.think.quantstarter.pilot.service.EthTradeService;
import com.think.quantstarter.rest.bean.swap.result.*;
import com.think.quantstarter.rest.enums.FuturesTransactionTypeEnum;
import com.think.quantstarter.rest.exception.APIException;
import com.think.quantstarter.rest.service.swap.SwapMarketAPIService;
import com.think.quantstarter.rest.service.swap.SwapUserAPIServive;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author mpthink
 * @date 2020/10/13 17:36
 */
@SpringBootTest
public class TradeTest {

    private static final String eth_instrument_id = "ETH-USDT-SWAP";

    @Resource
    private SwapUserAPIServive swapUserAPIServive;

    @Resource
    private EthTradeService ethTradeService;

    @Resource
    private SwapMarketAPIService swapMarketAPIService;


    @Test
    public void test2(){
        String holds = swapUserAPIServive.getHolds(eth_instrument_id);
    }

    @Test
    public void testAccount(){
        AccountsInfo ethAccountInfo = ethTradeService.getEthAccountInfo();
        System.out.println(ethAccountInfo);
        String ticker = swapMarketAPIService.getTickerApi(eth_instrument_id);
        TickerVO tickerVO = JSON.parseObject(ticker, TickerVO.class);
        Double total = Double.valueOf(ethAccountInfo.getTotal_avail_balance());
        Double last = tickerVO.getLast();
        int planSize = (int) (total * 100 * 0.25 / last);
        System.out.println(planSize);
    }

    @Test
    public void test(){
        //下单
        PerOrderResult order1 = ethTradeService.order(FuturesTransactionTypeEnum.OPEN_SHORT);
        String order_id = order1.getOrder_id();
        System.out.println("order_id: " + order_id);
        System.out.println("order1: " + order1);
        //获取下单价格
        OrderInfo orderInfo = ethTradeService.getOrderInfo(order_id);
        System.out.println("order price: " + Double.valueOf(orderInfo.getPrice_avg()));
        System.out.println("orderInfo: " + orderInfo);
        //计划委托
        SwapOrderResultVO order = ethTradeService.swapOrderAlgo(FuturesTransactionTypeEnum.CLOSE_SHORT,"412","416");
        System.out.println("plan order: " + order);
        String algo_id = order.getData().getAlgo_id();

        //获取委托单信息--测试
        OrderInfo orderInfo3 = ethTradeService.getOrderInfo(order_id);
        System.out.println("orderInfo3: " + orderInfo3);

        //检查
        SwapOrders beforeCancel = ethTradeService.checkAlgoOrder(algo_id);
        System.out.println("check before: " + beforeCancel);
        //取消
        CancelAlgoOrder cancelOrderAlgo = ethTradeService.cancelOrderAlgo(Arrays.asList(algo_id));
        System.out.println("cancel: " + cancelOrderAlgo);
        //取消后检查
        SwapOrders afterCancel = ethTradeService.checkAlgoOrder(algo_id);
        System.out.println("check after: " + afterCancel);

        //获取委托单信息--测试
        OrderInfo orderInfo4 = ethTradeService.getOrderInfo(order_id);
        System.out.println("orderInfo4: " + orderInfo4);

        //计划委托2
        SwapOrderResultVO order2 = ethTradeService.swapOrderAlgo(FuturesTransactionTypeEnum.CLOSE_SHORT,"412","416");
        System.out.println("plan order: " + order);
        String algo_id2 = order.getData().getAlgo_id();

        //获取委托单信息--测试
        OrderInfo orderInfo_temp = ethTradeService.getOrderInfo(algo_id2);
        System.out.println("order price: " + orderInfo_temp);
        try{
            //关单测试，是否有止盈止损单就不能下单了
            PerOrderResult order3 = ethTradeService.order(FuturesTransactionTypeEnum.CLOSE_SHORT);
            System.out.println("order3: "+ order3);
        }catch (APIException e) {
            System.out.println("不能下单了");
        }

        //取消计划
        ethTradeService.cancelOrderAlgo(Collections.singletonList(algo_id2));

        //再次下单
        PerOrderResult order4 = ethTradeService.order(FuturesTransactionTypeEnum.CLOSE_SHORT);
        System.out.println("order4: "+ order4);


    }

}
