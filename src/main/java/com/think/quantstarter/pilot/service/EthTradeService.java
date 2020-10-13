package com.think.quantstarter.pilot.service;

import com.think.quantstarter.rest.bean.swap.param.PpOrder;
import com.think.quantstarter.rest.bean.swap.result.PerOrderResult;
import com.think.quantstarter.rest.enums.FuturesTransactionTypeEnum;
import com.think.quantstarter.rest.service.swap.SwapTradeAPIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author mpthink
 * @date 2020/10/13 17:02
 */
@Slf4j
@Service
public class EthTradeService {

    private static final String eth_instrument_id = "ETH-USDT-SWAP";
    private static final String order_size = "1";
    private static final String order_type = "4"; //4：市价委托

    @Resource
    private SwapTradeAPIService swapTradeAPIService;

    /**
     * 所有单据都市价成交
     * @param type
     * @return
     */
    public PerOrderResult order(FuturesTransactionTypeEnum type){
        PpOrder order = PpOrder.builder()
                .size(order_size)
                .type(String.valueOf(type.code()))
                .instrument_id(eth_instrument_id)
                .order_type(order_type)
                .build();
        return swapTradeAPIService.order(order);
    }


}
