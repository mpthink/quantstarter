package com.think.quantstarter.rest.bean.swap.result;

import lombok.Data;

/**
 * @author mpthink
 * @date 2020/10/14 16:04
 */
@Data
public class OrderInfo {
    private String instrument_id;
    private String client_oid;
    private String size;
    private String timestamp;
    private String filled_qty;
    private String fee;
    private String order_id;
    private String price;//委托价格
    private String price_avg; //成交均价
    private String type;
    private String contract_val;
    private String order_type;
    private String state;//2:完全成交
    private String trigger_price;
    private String leverage;
}
