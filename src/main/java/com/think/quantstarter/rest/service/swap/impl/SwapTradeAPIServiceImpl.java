package com.think.quantstarter.rest.service.swap.impl;

import com.think.quantstarter.rest.bean.swap.param.*;
import com.think.quantstarter.rest.client.APIClient;
import com.think.quantstarter.rest.service.swap.SwapTradeAPIService;
import com.think.quantstarter.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Slf4j
@Service
public class SwapTradeAPIServiceImpl implements SwapTradeAPIService {

    @Resource
    private APIClient client;
    private SwapTradeAPI api;

    @PostConstruct
    private void init() {
        api = client.createService(SwapTradeAPI.class);
    }

    /**
     * 下单
     *
     * @param ppOrder
     * @return
     */
    @Override
    public Object order(PpOrder ppOrder) {
        System.out.println("下单参数：：：：：：");
        System.out.println(JsonUtils.convertObject(ppOrder, PpOrder.class));
        return this.client.executeSync(this.api.order(ppOrder));
    }

    /**
     * 批量下单
     *
     * @param ppOrders
     * @return
     */
    @Override
    public String orders(PpOrders ppOrders) {
        return this.client.executeSync(this.api.orders(JsonUtils.convertObject(ppOrders, PpOrders.class)));
    }

    /**
     * 撤单
     *
     * @param instrument_id
     * @param order_id
     * @return
     */
    @Override
    public String cancelOrderByOrderId(String instrument_id, String order_id) {
        return this.client.executeSync(this.api.cancelOrderByOrderId(instrument_id, order_id));
    }

    @Override
    public String cancelOrderByClientOid(String instrument_id, String client_oid) {
        return this.client.executeSync(this.api.cancelOrderByClientOid(instrument_id, client_oid));
    }

    /**
     * 批量撤单
     *
     * @param instrumentId
     * @param ppCancelOrderVO
     * @return
     */
    @Override
    public String cancelOrders(String instrumentId, PpCancelOrderVO ppCancelOrderVO) {
        return this.client.executeSync(this.api.cancelOrders(instrumentId, JsonUtils.convertObject(ppCancelOrderVO, PpCancelOrderVO.class)));
    }

    @Override
    public String amendOrder(String instrumentId, AmendOrder amendOrder) {
        return this.client.executeSync(this.api.amendOrder(instrumentId, amendOrder));
    }

    @Override
    public String amendOrderByClientOid(String instrument_id, AmendOrder amendOrder) {
        return this.client.executeSync(this.api.amendOrderByClientOid(instrument_id, amendOrder));
    }

    @Override
    public String amendBatchOrderByOrderId(String instrument_id, AmendOrderParam amendOrder) {
        return this.client.executeSync(this.api.amendBatchOrderByOrderId(instrument_id, amendOrder));
    }

    @Override
    public String amendBatchOrderByClientOid(String instrument_id, AmendOrderParam amendOrder) {
        return this.client.executeSync(this.api.amendBatchOrderByClientOid(instrument_id, amendOrder));
    }

    //委托策略下单
    @Override
    public String swapOrderAlgo(SwapOrderParam swapOrderParam) {
        System.out.println("begin swapOrder-----");
        return this.client.executeSync(this.api.swapOrderAlgo(swapOrderParam));
    }

    //委托策略撤单
    @Override
    public String cancelOrderAlgo(CancelOrderAlgo cancelOrderAlgo) {
        System.out.println("canceling the algo order");
        return this.client.executeSync(this.api.cancelOrderAlgo(cancelOrderAlgo));
    }

    //获取委托单列表
    @Override
    public String getSwapOrders(String instrument_id, String order_type, String status, String algo_id, String before, String after, String limit) {
        return this.client.executeSync(this.api.getSwapOrders(instrument_id, order_type, status, algo_id, before, after, limit));
    }

    @Override
    public String closePosition(ClosePosition closePosition) {
        return this.client.executeSync(this.api.closePosition(closePosition));
    }

    @Override
    public String CancelAll(CancelAllParam cancelAllParam) {
        return this.client.executeSync(this.api.CancelAll(cancelAllParam));
    }
}
