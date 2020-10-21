package com.think.quantstarter.rest.bean.swap.result;

import lombok.Data;

/**
 * @author mpthink
 * @date 2020/10/20 20:01
 */
@Data
public class CancelOrder {
    private String order_id;
    private boolean result;
    private String client_oid;
    private String error_code;
    private String error_message;
}
