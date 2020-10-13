package com.think.quantstarter.rest.bean.swap.param;

import lombok.Data;

@Data
public class PpBatchOrder {

    /**
     * 由您设置的订单id来唯一标识您的订单
     */
    private String client_oid;
    /**
     * 下单数量
     */
    private String size;
    /**
     * 1:开多 2:开空 3:平多 4:平空
     */
    private String type;
    /**
     * 是否以对手价下单 0:不是 1:是
     */
    private String match_price;
    /**
     * 委托价格
     */
    private String price;

    private String order_type;



}
