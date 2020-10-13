package com.think.quantstarter.rest.bean.swap.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SwapOrderParam {
    //通用参数
    private String instrument_id;
    private String type;
    private String order_type;
    private String size;
    //计划委托参数
    private String trigger_price;
    private String algo_price;
    private String algo_type;
    //跟踪委托
    private String callback_rate;
    //冰山委托
    private String algo_variance;
    private String avg_amount;
    private String price_limit;
    //时间加权
    private String sweep_range;
    private String sweep_ratio;
    private String single_limit;
    private String time_interval;
    //止盈止损参数
    private String tp_trigger_price; //	止盈触发价格
    private String tp_price;//止盈委托价格
    private String tp_trigger_type;//1:限价 2:市场价；止盈触发价格类型，默认是限价；为市场价时，委托价格不必填；
    private String sl_trigger_price;//止损触发价格
    private String sl_price;//止损委托价格
    private String sl_trigger_type;//1:限价 2:市场价；止损触发价格类型，默认是限价；为市场价时，委托价格不必填；
}
