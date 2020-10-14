package com.think.quantstarter.rest.bean.swap.result;

import lombok.Data;

/**
 * @author mpthink
 * @date 2020/10/14 9:31
 */
@Data
public class CancelAlgoOrder {
    private String result;
    private String algo_ids;
    private String instrument_id;
    private String order_type;
}
