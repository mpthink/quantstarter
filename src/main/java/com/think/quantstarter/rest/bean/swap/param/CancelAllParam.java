package com.think.quantstarter.rest.bean.swap.param;

import lombok.Data;

@Data
public class CancelAllParam {
    private String instrument_id;
    private String direction;
}
