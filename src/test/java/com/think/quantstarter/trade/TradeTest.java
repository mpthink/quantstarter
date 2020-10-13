package com.think.quantstarter.trade;

import com.think.quantstarter.rest.service.swap.SwapUserAPIServive;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author mpthink
 * @date 2020/10/13 17:36
 */
@SpringBootTest
public class TradeTest {

    private SwapUserAPIServive swapUserAPIServive;

    @Test
    public void test(){

        String s = swapUserAPIServive.selectAccount("ETH-USD-SWAP");

        System.out.println(s);

    }

}
