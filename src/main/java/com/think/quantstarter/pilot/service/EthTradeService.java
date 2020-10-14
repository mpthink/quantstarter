package com.think.quantstarter.pilot.service;

import com.alibaba.fastjson.JSON;
import com.think.quantstarter.rest.bean.swap.param.CancelOrderAlgo;
import com.think.quantstarter.rest.bean.swap.param.PpOrder;
import com.think.quantstarter.rest.bean.swap.param.SwapOrderParam;
import com.think.quantstarter.rest.bean.swap.result.*;
import com.think.quantstarter.rest.enums.FuturesTransactionTypeEnum;
import com.think.quantstarter.rest.service.swap.SwapTradeAPIService;
import com.think.quantstarter.rest.service.swap.SwapUserAPIServive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author mpthink
 * @date 2020/10/13 17:02
 */
@Slf4j
@Service
public class EthTradeService {

    private static final String eth_instrument_id = "ETH-USDT-SWAP";
    private static final String order_size = "1";
    private static final String order_type = "4"; //4：市价委托
    private static final String trigger_type = "2"; //1:限价 2:市场价；止盈触发价格类型，默认是限价；为市场价时，委托价格不必填；
    private static final String algo_order_type = "5"; //5：止盈止损

    @Resource
    private SwapTradeAPIService swapTradeAPIService;
    @Resource
    private SwapUserAPIServive swapUserAPIServive;

    /**
     * 所有单据都市价成交
     * @param type
     * @return
     */
    public PerOrderResult order(FuturesTransactionTypeEnum type){
        PpOrder order = PpOrder.builder()
                .size(order_size)
                .type(String.valueOf(type.code()))
                .instrument_id(eth_instrument_id)
                .order_type(order_type)
                .build();
        return swapTradeAPIService.order(order);
    }

    /**
     * 所有单据都市价成交
     * @param type 1:开多 2:开空 3:平多 4:平空
     * @param tp_trigger_price 止盈触发价格
     * @param sl_trigger_price 止损触发价格
     * @return
     */
    public SwapOrderResultVO swapOrderAlgo(FuturesTransactionTypeEnum type, String tp_trigger_price, String sl_trigger_price){
        SwapOrderParam swapOrderParam = SwapOrderParam.builder()
                .instrument_id(eth_instrument_id)
                .type(String.valueOf(type.code()))
                .order_type(algo_order_type)
                .size(order_size)
                .tp_trigger_price(tp_trigger_price)
                .tp_trigger_type(trigger_type) //1:限价 2:市场价；止盈触发价格类型，默认是限价；为市场价时，委托价格不必填；
                .sl_trigger_price(sl_trigger_price)
                .sl_trigger_type(trigger_type)
                .build();
        return JSON.parseObject(swapTradeAPIService.swapOrderAlgo(swapOrderParam), SwapOrderResultVO.class);
    }

    public CancelAlgoOrder cancelOrderAlgo(List<String> algo_ids){
        CancelOrderAlgo cancelOrderAlgo = CancelOrderAlgo.builder()
                .instrument_id(eth_instrument_id)
                .algo_ids(algo_ids)
                .order_type(algo_order_type)
                .build();
        String orderAlgo = swapTradeAPIService.cancelOrderAlgo(cancelOrderAlgo);
        return JSON.parseObject(orderAlgo, CancelAlgoOrderVO.class).getData();
    }

    public SwapOrders checkAlgoOrder(String algo_id){
        String swapOrders = swapTradeAPIService.getSwapOrders(eth_instrument_id, algo_order_type, "", algo_id, "", "", "");
        return JSON.parseObject(swapOrders, SwapOrdersVO.class).getOrderStrategyVOS().get(0);
    }

    public OrderInfo getOrderInfo(String order_id){
        String info = swapUserAPIServive.selectOrderByOrderId(eth_instrument_id, order_id);
        return JSON.parseObject(info, OrderInfo.class);
    }

    public String getDealDetail(String order_id){
        String dealDetail = swapUserAPIServive.selectDealDetail(eth_instrument_id, order_id, "", "", "");
        return dealDetail;
    }

}
