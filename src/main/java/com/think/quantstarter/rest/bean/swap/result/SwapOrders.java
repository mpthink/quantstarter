package com.think.quantstarter.rest.bean.swap.result;

import lombok.Data;

/**
 * @author mpthink
 * @date 2020/10/13 20:24
 */
@Data
public class SwapOrders {
    private String algo_id;
    private String contract_val;
    private String created_at;
    private String instrument_id;
    private String last_fill_px;
    private String leverage;
    private String margin_for_unfilled;
    private String modifyTime;
    private String multiply;
    private String order_id;
    private String order_side;
    private String real_amount;
    private String real_price;
    private String size;
    private String sl_price;
    private String sl_trigger_price;
    private String sl_trigger_type;
    private String status;
    private String timestamp;
    private String tp_price;
    private String tp_trigger_price;
    private String tp_trigger_type;
    private String trigger_side;
    private String type;
    private String unitAmount;
}
