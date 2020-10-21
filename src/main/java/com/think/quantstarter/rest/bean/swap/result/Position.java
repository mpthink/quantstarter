package com.think.quantstarter.rest.bean.swap.result;

import lombok.Data;

/**
 * @author mpthink
 * @date 2020/10/21 15:06
 */
@Data
public class Position {
    private String margin_mode;
    private String liquidation_price;
    private String position;
    private String avail_position;
    private String margin;
    private String avg_cost;
    private String settlement_price;
    private String instrument_id;
    private String leverage;
    private String realized_pnl;
    private String side;
    private String timestamp;
    private String maint_margin_ratio;
    private String settled_pnl;
    private String last;
    private String unrealized_pnl;
}
