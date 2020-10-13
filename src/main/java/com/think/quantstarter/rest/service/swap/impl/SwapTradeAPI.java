package com.think.quantstarter.rest.service.swap.impl;

import com.alibaba.fastjson.JSONObject;
import com.think.quantstarter.rest.bean.swap.param.*;
import com.think.quantstarter.rest.bean.swap.result.PerOrderResult;
import retrofit2.Call;
import retrofit2.http.*;

public interface SwapTradeAPI {
    //下单
    @POST("/api/swap/v3/order")
    Call<PerOrderResult> order(@Body PpOrder ppOrder);
    //批量下单
    @POST("/api/swap/v3/orders")
    Call<String> orders(@Body JSONObject ppOrders);
    //撤单
    @POST("/api/swap/v3/cancel_order/{instrument_id}/{order_id}")
    Call<String> cancelOrderByOrderId(@Path("instrument_id") String instrument_id, @Path("order_id") String order_id);
    @POST("/api/swap/v3/cancel_order/{instrument_id}/{client_oid}")
    Call<String> cancelOrderByClientOid(@Path("instrument_id") String instrument_id, @Path("client_oid") String client_oid);

    //批量撤单
    @POST("/api/swap/v3/cancel_batch_orders/{instrument_id}")
    Call<String> cancelOrders(@Path("instrument_id") String instrument_id, @Body JSONObject ppOrders);

    //修改订单
    @POST("/api/swap/v3/amend_order/{instrument_id}")
    Call<String> amendOrder(@Path("instrument_id") String instrument_id, @Body AmendOrder amendOrder);

    //修改订单
    @POST("/api/swap/v3/amend_order/{instrument_id}")
    Call<String> amendOrderByClientOid(@Path("instrument_id") String instrument_id, @Body AmendOrder amendOrder);

    //批量修改订单
    @POST("/api/swap/v3/amend_batch_orders/{instrument_id}")
    Call<String> amendBatchOrderByOrderId(@Path("instrument_id") String instrument_id, @Body AmendOrderParam amendOrder);

    @POST("/api/swap/v3/amend_batch_orders/{instrument_id}")
    Call<String> amendBatchOrderByClientOid(@Path("instrument_id") String instrument_id, @Body AmendOrderParam amendOrder);



    /**
     * 策略下单
     * @param swapOrderParam
     * @return
     */
    @POST("/api/swap/v3/order_algo")
    Call<String> swapOrderAlgo(@Body SwapOrderParam swapOrderParam);

    /**
     * 策略撤单
     * @param cancelOrderAlgo
     * @return
     */
    @POST("/api/swap/v3/cancel_algos")
    Call<String> cancelOrderAlgo(@Body CancelOrderAlgo cancelOrderAlgo);

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


    @GET("/api/swap/v3/order_algo/{instrument_id}")
    Call<String> getSwapOrders(@Path("instrument_id") String instrument_id,
                               @Query("order_type") String order_type,
                               @Query("status") String status,
                               @Query("algo_id") String algo_id,
                               @Query("before") String before,
                               @Query("after") String after,
                               @Query("limit") String limit);

    /*市价全平*/
    @POST("/api/swap/v3/close_position")
    Call<String> closePosition(@Body ClosePosition closePosition);

    /*撤销所有平仓挂单*/
    @POST("/api/swap/v3/cancel_all")
    Call<String> CancelAll(@Body CancelAllParam cancelAllParam);

}