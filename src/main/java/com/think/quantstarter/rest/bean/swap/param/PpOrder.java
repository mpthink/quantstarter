package com.think.quantstarter.rest.bean.swap.param;

import lombok.Data;

@Data
public class PpOrder {
    /**
     * 由您设置的订单id来唯一标识您的订单
     */
    private String client_oid;
    /**
     * 下单数量
     */
    private String size;
    /**
     * 1:开多 2:开空 3:平多 4:平空
     */
    private String type;
    /**
     * 是否以对手价下单 0:不是 1:是
     */
    private String match_price;
    /**
     * 委托价格
     */
    private String price;
    /**
     * 合约名称，如BTC-USD-SWAP
     */
    private String instrument_id;
    /**
     * 	参数填数字，0：普通委托（order type不填或填0都是普通委托） 1：只做Maker（Post only） 2：全部成交或立即取消（FOK） 3：立即成交并取消剩余（IOC）
     */
    private String order_type;

}
