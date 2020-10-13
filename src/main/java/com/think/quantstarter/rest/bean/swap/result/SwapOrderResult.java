package com.think.quantstarter.rest.bean.swap.result;

import lombok.Data;

/**
 * @author mpthink
 * @date 2020/10/13 19:39
 */
@Data
public class SwapOrderResult {
    private String result;
    private String algo_id;
    private String error_message;
    private String error_code;
    private String instrument_id;
    private String order_type;
}
