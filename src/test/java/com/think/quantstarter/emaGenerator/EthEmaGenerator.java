package com.think.quantstarter.emaGenerator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.think.quantstarter.analysis.utils.CandleUtil;
import com.think.quantstarter.dataCollect.entity.EthCandles1h;
import com.think.quantstarter.dataCollect.entity.EthCandles4h;
import com.think.quantstarter.dataCollect.entity.EthCandles5m;
import com.think.quantstarter.dataCollect.mapper.EthCandles1hMapper;
import com.think.quantstarter.dataCollect.mapper.EthCandles4hMapper;
import com.think.quantstarter.dataCollect.mapper.EthCandles5mMapper;
import com.think.quantstarter.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author mpthink
 * @date 2020/9/16 20:35
 */
@SpringBootTest
@Slf4j
public class EthEmaGenerator {

    /**
     * 生成EMA5, EMA10 和 EMA20
     * 1. 每次获取最老的一条数据，然后+一个周期（第一条数据EMA就等于收盘价）
     * 2. 从数据库中获取当前日期之前的5条数据（包含当前数据）
     * 3. 获取之前数据的EMA值+当前数据的收盘价，计算当前数据的EMA值
     * 4. 更新数据库，进行下一个循环
     */
    @Resource
    private EthCandles5mMapper ethCandles5mMapper;
    @Resource
    private EthCandles1hMapper ethCandles1hMapper;
    @Resource
    private EthCandles4hMapper ethCandles4hMapper;

    @Test
    public void generateEmaForEth5m() throws ParseException {
        generateEMA5m(5);
        generateEMA5m(10);
        generateEMA5m(20);
    }

    private void generateEMA5m(int ema) throws ParseException {
        QueryWrapper<EthCandles5m> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("candle_time");
        wrapper.last("limit 1");
        EthCandles5m oldest = ethCandles5mMapper.selectOne(wrapper);
        switch (ema){
            case 5:
                oldest.setEma5(oldest.getClose());
                break;
            case 10:
                oldest.setEma10(oldest.getClose());
                break;
            case 20:
                oldest.setEma20(oldest.getClose());
                break;
        }
        oldest.setEma5(oldest.getClose());
        oldest.updateById();
        String start = oldest.getCandleTime();
        while (DateUtils.parseUTCTime(start).before(new Date())){
            start = DateUtils.addMinutes(start, 5);
            EthCandles5m latest = ethCandles5mMapper.selectById(start);
            if(latest == null){
                return;
            }
            List<Double> datas = new ArrayList<>();
            switch (ema){
                case 5:
                    datas.add(oldest.getEma5());
                    datas.add(latest.getClose());
                    latest.setEma5(CandleUtil.getEMA(datas, ema));
                    break;
                case 10:
                    datas.add(oldest.getEma10());
                    datas.add(latest.getClose());
                    latest.setEma10(CandleUtil.getEMA(datas, ema));
                    break;
                case 20:
                    datas.add(oldest.getEma20());
                    datas.add(latest.getClose());
                    latest.setEma20(CandleUtil.getEMA(datas, ema));
                    break;
            }
            latest.updateById();
            oldest = latest;
        }
    }

    @Test
    public void generateEmaForEth1h() throws ParseException {
        generateEMA1h(5);
        generateEMA1h(10);
        generateEMA1h(20);
    }

    @Test
    public void generateEmaForEth4h() throws ParseException {
        generateEMA4h(5);
        generateEMA4h(10);
        generateEMA4h(20);
    }

    private void generateEMA1h(int ema) throws ParseException {
        QueryWrapper<EthCandles1h> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("candle_time");
        wrapper.last("limit 1");
        EthCandles1h oldest = ethCandles1hMapper.selectOne(wrapper);
        switch (ema){
            case 5:
                oldest.setEma5(oldest.getClose());
                break;
            case 10:
                oldest.setEma10(oldest.getClose());
                break;
            case 20:
                oldest.setEma20(oldest.getClose());
                break;
        }
        oldest.setEma5(oldest.getClose());
        oldest.updateById();
        String start = oldest.getCandleTime();
        while (DateUtils.parseUTCTime(start).before(new Date())){
            start = DateUtils.addMinutes(start, 60);
            EthCandles1h latest = ethCandles1hMapper.selectById(start);
            if(latest == null){
                return;
            }
            List<Double> datas = new ArrayList<>();
            switch (ema){
                case 5:
                    datas.add(oldest.getEma5());
                    datas.add(latest.getClose());
                    latest.setEma5(CandleUtil.getEMA(datas, ema));
                    break;
                case 10:
                    datas.add(oldest.getEma10());
                    datas.add(latest.getClose());
                    latest.setEma10(CandleUtil.getEMA(datas, ema));
                    break;
                case 20:
                    datas.add(oldest.getEma20());
                    datas.add(latest.getClose());
                    latest.setEma20(CandleUtil.getEMA(datas, ema));
                    break;
            }
            latest.updateById();
            oldest = latest;
        }
    }

    private void generateEMA4h(int ema) throws ParseException {
        QueryWrapper<EthCandles4h> wrapper = new QueryWrapper<>();
        wrapper.orderByAsc("candle_time");
        wrapper.last("limit 1");
        EthCandles4h oldest = ethCandles4hMapper.selectOne(wrapper);
        switch (ema){
            case 5:
                oldest.setEma5(oldest.getClose());
                break;
            case 10:
                oldest.setEma10(oldest.getClose());
                break;
            case 20:
                oldest.setEma20(oldest.getClose());
                break;
        }
        oldest.setEma5(oldest.getClose());
        oldest.updateById();
        String start = oldest.getCandleTime();
        while (DateUtils.parseUTCTime(start).before(new Date())){
            start = DateUtils.addMinutes(start, 240);
            EthCandles4h latest = ethCandles4hMapper.selectById(start);
            if(latest == null){
                return;
            }
            List<Double> datas = new ArrayList<>();
            switch (ema){
                case 5:
                    datas.add(oldest.getEma5());
                    datas.add(latest.getClose());
                    latest.setEma5(CandleUtil.getEMA(datas, ema));
                    break;
                case 10:
                    datas.add(oldest.getEma10());
                    datas.add(latest.getClose());
                    latest.setEma10(CandleUtil.getEMA(datas, ema));
                    break;
                case 20:
                    datas.add(oldest.getEma20());
                    datas.add(latest.getClose());
                    latest.setEma20(CandleUtil.getEMA(datas, ema));
                    break;
            }
            latest.updateById();
            oldest = latest;
        }
    }

}
