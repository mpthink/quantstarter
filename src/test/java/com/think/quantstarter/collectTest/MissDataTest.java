package com.think.quantstarter.collectTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.think.quantstarter.dataCollect.entity.EthCandles5m;
import com.think.quantstarter.dataCollect.mapper.EthCandles5mMapper;
import com.think.quantstarter.utils.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;

/**
 * @author mpthink
 * @date 2020/9/15 19:13
 */
@SpringBootTest
public class MissDataTest {

    @Resource
    private EthCandles5mMapper ethCandles5mMapper;

    @Test
    public void findMiss5m() throws ParseException {
        QueryWrapper<EthCandles5m> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("candle_time");
        wrapper.last("limit 1");
        EthCandles5m ethCandles1m = ethCandles5mMapper.selectOne(wrapper);
        String candleTime = ethCandles1m.getCandleTime();
        int add = 5;
        while(DateUtils.parseUTCTime(candleTime).before(new Date())){
            candleTime = DateUtils.addMinutes(candleTime,add);
            wrapper = new QueryWrapper<>();
            wrapper.eq("candle_time", candleTime);
            EthCandles5m temp = ethCandles5mMapper.selectOne(wrapper);
            if(temp == null){
                System.out.println("Time missed: " + candleTime);
            }
        }
    }

}
