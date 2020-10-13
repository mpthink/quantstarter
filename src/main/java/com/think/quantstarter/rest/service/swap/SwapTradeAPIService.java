package com.think.quantstarter.rest.service.swap;


import com.think.quantstarter.rest.bean.swap.param.*;
import com.think.quantstarter.rest.bean.swap.result.PerOrderResult;

public interface SwapTradeAPIService {
    /**
     * 下单
     * @param ppOrder
     * @return
     */
    PerOrderResult order(PpOrder ppOrder);

    /**
     * 批量下单
     * @param ppOrders
     * @return
     */
    String orders(PpOrders ppOrders);

    /**
     * 撤单
     * @param instrument_id
     * @param order_id
     * @return
     */
    String cancelOrderByOrderId(String instrument_id, String order_id);

    String cancelOrderByClientOid(String instrument_id, String client_oid);

    /**
     * 批量撤单
     * @param instrument_id
     * @param ppCancelOrderVO
     * @return
     */
    String cancelOrders(String instrument_id, PpCancelOrderVO ppCancelOrderVO);

    //修改订单
    String amendOrder(String instrument_id, AmendOrder amendOrder);


    String amendOrderByClientOid(String instrument_id,AmendOrder amendOrder);

    String amendBatchOrderByOrderId(String instrument_id, AmendOrderParam amendOrder);

    String amendBatchOrderByClientOid(String instrument_id, AmendOrderParam amendOrder);

    /**
     * 策略委托下单
     * @param swapOrderParam
     * @return
     */
    String swapOrderAlgo(SwapOrderParam swapOrderParam);

    /**
     * 策略委托撤单
     * @param cancelOrderAlgo
     * @return
     */
    String cancelOrderAlgo(CancelOrderAlgo cancelOrderAlgo);

    /**
     * 查看策略委托订单
     * @param instrument_id
     * @param order_type
     * @param status
     * @param algo_id
     * @param before
     * @param after
     * @param limit
     * @return
     */
    String getSwapOrders(String instrument_id,
                         String order_type,
                         String status,
                         String algo_id,
                         String before,
                         String after,
                         String limit);

    //市价全平
    String closePosition(ClosePosition closePosition);

    String CancelAll(CancelAllParam cancelAllParam);
}
