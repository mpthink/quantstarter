package com.think.quantstarter.pilot.bean;

import com.think.quantstarter.rest.enums.FuturesTransactionTypeEnum;
import lombok.Builder;
import lombok.Data;

/**
 * @author mpthink
 * @date 2020/10/14 11:37
 */
@Data
@Builder
public class OrderRecord {
    /**
     * 下单order id
     */
    private String order_id;
    /**
     * 止盈止损单 id
     */
    private String algo_id;
    /**
     * 下单时间
     */
    private String timestamp;
    /**
     * 下单类型  LONG, SHORT
     */
    private FuturesTransactionTypeEnum order_type;

}
