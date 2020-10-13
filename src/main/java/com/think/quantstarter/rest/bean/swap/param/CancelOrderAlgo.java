package com.think.quantstarter.rest.bean.swap.param;

import lombok.Data;

import java.util.List;

@Data
public class CancelOrderAlgo {
    private String instrument_id;
    private List<String> algo_ids;
    //1：计划委托 2：跟踪委托 3：冰山委托 4：时间加权 5：止盈止损
    private String order_type;
}
