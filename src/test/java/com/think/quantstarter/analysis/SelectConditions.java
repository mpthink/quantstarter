package com.think.quantstarter.analysis;

import lombok.Builder;
import lombok.Data;

/**
 * @author mpthink
 * @date 2020/9/24 22:34
 */
@Data
@Builder
public class SelectConditions {
    double lossN;
    int lossM;
    boolean hour1Ema;
    boolean hour4Ema;
    boolean hour1Trend;
    boolean hour4Trend;
    int handInTime;
    int intervalBuy;
}
