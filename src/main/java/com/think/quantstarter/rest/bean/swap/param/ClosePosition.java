package com.think.quantstarter.rest.bean.swap.param;

import lombok.Data;

@Data
public class ClosePosition {
    private String instrument_id;
    private String direction;
}
