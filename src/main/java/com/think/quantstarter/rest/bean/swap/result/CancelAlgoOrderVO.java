package com.think.quantstarter.rest.bean.swap.result;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author mpthink
 * @date 2020/10/14 9:31
 */
@Data
public class CancelAlgoOrderVO {
    private String code;
    private CancelAlgoOrder data;
    private String detailMsg;
    private String error_code;
    private String error_message;
    private String msg;

    public static void main(String[] args) {
        String example = "{\"code\":0,\"data\":{\"result\":\"success\",\"algo_ids\":\"[609283666041540610]\",\"instrument_id\":\"ETH-USDT-SWAP\",\"order_type\":\"5\"},\"detailMsg\":\"\",\"error_code\":\"0\",\"error_message\":\"\",\"msg\":\"\"}";
        CancelAlgoOrderVO cancelAlgoOrderVO = JSON.parseObject(example, CancelAlgoOrderVO.class);
        System.out.println(cancelAlgoOrderVO.getData().getAlgo_ids());
    }
}
