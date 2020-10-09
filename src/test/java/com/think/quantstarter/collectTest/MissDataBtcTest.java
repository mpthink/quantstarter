package com.think.quantstarter.collectTest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.think.quantstarter.dataCollect.entity.BtcCandles1h;
import com.think.quantstarter.dataCollect.entity.BtcCandles4h;
import com.think.quantstarter.dataCollect.entity.BtcCandles5m;
import com.think.quantstarter.dataCollect.mapper.BtcCandles1hMapper;
import com.think.quantstarter.dataCollect.mapper.BtcCandles4hMapper;
import com.think.quantstarter.dataCollect.mapper.BtcCandles5mMapper;
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
public class MissDataBtcTest {

    @Resource
    private BtcCandles5mMapper btcCandles5mMapper;
    @Resource
    private BtcCandles1hMapper btcCandles1hMapper;
    @Resource
    private BtcCandles4hMapper btcCandles4hMapper;



    @Test
    public void findMiss5m() throws ParseException {
        QueryWrapper<BtcCandles5m> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("candle_time");
        wrapper.last("limit 1");
        BtcCandles5m btcCandles1m = btcCandles5mMapper.selectOne(wrapper);
        String candleTime = btcCandles1m.getCandleTime();
        int add = 5;
        while(DateUtils.parseUTCTime(candleTime).before(new Date())){
            candleTime = DateUtils.addMinutes(candleTime,add);
            wrapper = new QueryWrapper<>();
            wrapper.eq("candle_time", candleTime);
            BtcCandles5m temp = btcCandles5mMapper.selectOne(wrapper);
            if(temp == null){
                System.out.println("Time missed: " + candleTime);
            }
        }
    }

    @Test
    public void findMiss1h() throws ParseException {
        QueryWrapper<BtcCandles1h> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("candle_time");
        wrapper.last("limit 1");
        BtcCandles1h btcCandles1m = btcCandles1hMapper.selectOne(wrapper);
        String candleTime = btcCandles1m.getCandleTime();
        int add = 60;
        while(DateUtils.parseUTCTime(candleTime).before(new Date())){
            candleTime = DateUtils.addMinutes(candleTime,add);
            wrapper = new QueryWrapper<>();
            wrapper.eq("candle_time", candleTime);
            BtcCandles1h temp = btcCandles1hMapper.selectOne(wrapper);
            if(temp == null){
                System.out.println("Time missed: " + candleTime);
            }
        }
    }

    @Test
    public void findMiss4h() throws ParseException {
        QueryWrapper<BtcCandles4h> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("candle_time");
        wrapper.last("limit 1");
        BtcCandles4h btcCandles1m = btcCandles4hMapper.selectOne(wrapper);
        String candleTime = btcCandles1m.getCandleTime();
        int add = 240;
        while(DateUtils.parseUTCTime(candleTime).before(new Date())){
            candleTime = DateUtils.addMinutes(candleTime,add);
            wrapper = new QueryWrapper<>();
            wrapper.eq("candle_time", candleTime);
            BtcCandles4h temp = btcCandles4hMapper.selectOne(wrapper);
            if(temp == null){
                System.out.println("Time missed: " + candleTime);
            }
        }
    }

}
