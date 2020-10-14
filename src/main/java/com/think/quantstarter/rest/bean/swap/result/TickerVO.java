package com.think.quantstarter.rest.bean.swap.result;

/**
 * @author mpthink
 * @date 2020/10/14 9:51
 */
public class TickerVO {
    private String instrument_id;
    private String timestamp;
    private Double last;
    private Double high_24h;
    private Double low_24h;
    private Double volume_24h;
    private Double volume_token_24h;
    private Double best_ask;
    private Double best_bid;
    private Double last_qty;
    private Double best_bid_size;
    private Double best_ask_size;
}
