package com.think.quantstarter.service;

import com.think.quantstarter.config.OkexConfig;
import com.think.quantstarter.rest.service.swap.SwapMarketAPIService;
import com.think.quantstarter.websocket.WebSocketClient;
import com.think.quantstarter.websocket.constant.ChannelConstants;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author mpthink
 * @date 2020/8/11 15:03
 */
@Service
public class DataCollectService {

    @Resource
    private SwapMarketAPIService swapMarketAPIService;

    @Resource
    private OkexConfig okexConfig;

    @PostConstruct
    public void subscribeWSS(){
        WebSocketClient webSocketClient = new WebSocketClient();
        webSocketClient.connection(okexConfig.getWssUrl());
        webSocketClient.subscribe(ChannelConstants.subscribes);
    }

}
