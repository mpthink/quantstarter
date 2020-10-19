package com.think.quantstarter.rest.bean.swap.result;

import lombok.Data;

/**
 * @author mpthink
 * @date 2020/10/19 16:53
 */
@Data
public class AccountsInfo {
    private String equity;
    private String fixed_balance;
    private String instrument_id;
    private String currency;
    private String underlying;
    private String maint_margin_ratio;
    private String margin;
    private String margin_frozen;
    private String margin_mode;
    private String margin_ratio;
    private String realized_pnl;
    private String timestamp;
    private String total_avail_balance;
    private String unrealized_pnl;
    private String max_withdraw;
}
