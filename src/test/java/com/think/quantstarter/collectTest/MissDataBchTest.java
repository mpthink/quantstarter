package com.think.quantstarter.collectTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.think.quantstarter.dataCollect.entity.BchCandles1h;
import com.think.quantstarter.dataCollect.entity.BchCandles4h;
import com.think.quantstarter.dataCollect.entity.BchCandles5m;
import com.think.quantstarter.dataCollect.mapper.BchCandles1hMapper;
import com.think.quantstarter.dataCollect.mapper.BchCandles4hMapper;
import com.think.quantstarter.dataCollect.mapper.BchCandles5mMapper;
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
public class MissDataBchTest {

    @Resource
    private BchCandles5mMapper bchCandles5mMapper;
    @Resource
    private BchCandles1hMapper bchCandles1hMapper;
    @Resource
    private BchCandles4hMapper bchCandles4hMapper;



    @Test
    public void findMiss5m() throws ParseException {
        QueryWrapper<BchCandles5m> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("candle_time");
        wrapper.last("limit 1");
        BchCandles5m bchCandles1m = bchCandles5mMapper.selectOne(wrapper);
        String candleTime = bchCandles1m.getCandleTime();
        int add = 5;
        while(DateUtils.parseUTCTime(candleTime).before(new Date())){
            candleTime = DateUtils.addMinutes(candleTime,add);
            wrapper = new QueryWrapper<>();
            wrapper.eq("candle_time", candleTime);
            BchCandles5m temp = bchCandles5mMapper.selectOne(wrapper);
            if(temp == null){
                System.out.println("Time missed: " + candleTime);
            }
        }
    }

    @Test
    public void findMiss1h() throws ParseException {
        QueryWrapper<BchCandles1h> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("candle_time");
        wrapper.last("limit 1");
        BchCandles1h bchCandles1m = bchCandles1hMapper.selectOne(wrapper);
        String candleTime = bchCandles1m.getCandleTime();
        int add = 60;
        while(DateUtils.parseUTCTime(candleTime).before(new Date())){
            candleTime = DateUtils.addMinutes(candleTime,add);
            wrapper = new QueryWrapper<>();
            wrapper.eq("candle_time", candleTime);
            BchCandles1h temp = bchCandles1hMapper.selectOne(wrapper);
            if(temp == null){
                System.out.println("Time missed: " + candleTime);
            }
        }
    }

    @Test
    public void findMiss4h() throws ParseException {
        QueryWrapper<BchCandles4h> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("candle_time");
        wrapper.last("limit 1");
        BchCandles4h bchCandles1m = bchCandles4hMapper.selectOne(wrapper);
        String candleTime = bchCandles1m.getCandleTime();
        int add = 240;
        while(DateUtils.parseUTCTime(candleTime).before(new Date())){
            candleTime = DateUtils.addMinutes(candleTime,add);
            wrapper = new QueryWrapper<>();
            wrapper.eq("candle_time", candleTime);
            BchCandles4h temp = bchCandles4hMapper.selectOne(wrapper);
            if(temp == null){
                System.out.println("Time missed: " + candleTime);
            }
        }
    }

}
