package com.think.quantstarter.rest.bean.swap.result;

import lombok.Data;

/**
 * @author mpthink
 * @date 2020/10/13 19:06
 */
@Data
public class PerOrderResult {
    String order_id;
    String client_oid;
    String error_code;
    String error_message;
    boolean result;
}
