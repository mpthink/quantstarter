package com.think.quantstarter.rest.bean.swap.param;

import lombok.Data;

@Data
public class AmendOrder {
    private String cancel_on_fail;
    private String order_id;
    private String client_oid;
    private String request_id;
    private String new_size;
    private String new_price;
}
