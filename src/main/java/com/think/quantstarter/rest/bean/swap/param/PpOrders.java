package com.think.quantstarter.rest.bean.swap.param;

import lombok.Data;

import java.util.List;

@Data
public class PpOrders {

    private String instrument_id ;
    private List<PpBatchOrder> order_data ;

}
