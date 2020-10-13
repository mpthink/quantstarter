package com.think.quantstarter.rest.bean.swap.result;

import lombok.Data;

/**
 * @author mpthink
 * @date 2020/10/13 19:37
 */
@Data
public class SwapOrderResultVO {

    private String code;
    private SwapOrderResult data;
    private String detailMsg;
    private String error_code;
    private String error_message;
    private String msg;
}
