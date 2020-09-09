package com.think.quantstarter.collectTest;

import com.think.quantstarter.dataCollect.entity.BtcCandles1d;
import com.think.quantstarter.dataCollect.job.InitGetBtcCandlesJob;
import com.think.quantstarter.dataCollect.service.IBtcCandles1dService;
import com.think.quantstarter.rest.constant.APIConstants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.ParseException;

/**
 * @author mpthink
 * @date 2020/9/8 19:08
 */
@SpringBootTest
public class CollectBTCServiceTest {

    @Resource
    private InitGetBtcCandlesJob job;

    @Resource
    private IBtcCandles1dService btcCandles1dService;

    @Test
    public void test() throws ParseException {
        job.initGetBTC(APIConstants.GRANULARITY1DAY, BtcCandles1d.class, btcCandles1dService);
    }
}
