package com.think.quantstarter.websocket.constant;

import java.util.Arrays;
import java.util.List;

/**
 * @author mpthink
 * @date 2020/8/11 15:10
 */
public class ChannelConstants {

    public static final List<String> subscribes = Arrays.asList(
            "swap/candle180s:BTC-USD-SWAP",
            "swap/candle300s:BTC-USD-SWAP",
            "swap/candle900s:BTC-USD-SWAP",
            "swap/candle1800s:BTC-USD-SWAP",
            "swap/candle3600s:BTC-USD-SWAP",
            "swap/candle14400s:BTC-USD-SWAP",
            "swap/candle86400s:BTC-USD-SWAP",
            "swap/candle604800s:BTC-USD-SWAP",
            "swap/ticker:BTC-USD-SWAP");
}
