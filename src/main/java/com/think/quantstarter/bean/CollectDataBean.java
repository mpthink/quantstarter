package com.think.quantstarter.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author mpthink
 * @date 2020/8/11 21:01
 */
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CollectDataBean {
    private String candle3Min;
    private String candle5Min;
    private String candle15Min;
    private String candle30Min;
    private String candle1Hour;
    private String candle4Hour;
    private String candle1Day;
    private String candle1Week;
    private String ticker;
}
