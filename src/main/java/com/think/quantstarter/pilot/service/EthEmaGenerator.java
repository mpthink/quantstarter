package com.think.quantstarter.pilot.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.think.quantstarter.analysis.utils.CandleUtil;
import com.think.quantstarter.dataCollect.entity.EthCandles1h;
import com.think.quantstarter.dataCollect.entity.EthCandles4h;
import com.think.quantstarter.dataCollect.entity.EthCandles5m;
import com.think.quantstarter.dataCollect.service.IEthCandles1hService;
import com.think.quantstarter.dataCollect.service.IEthCandles4hService;
import com.think.quantstarter.dataCollect.service.IEthCandles5mService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mpthink
 * @date 2020/10/10 17:02
 * 每次获取5分钟结果后，对最新的结果进行ema计算，1h和4h的ema就不需要再次计算current的ema
 */
@Service
@Slf4j
public class EthEmaGenerator {

    @Resource
    private IEthCandles5mService ethCandles5mService;
    @Resource
    private IEthCandles1hService ethCandles1hService;
    @Resource
    private IEthCandles4hService ethCandles4hService;

    private static int records  = 10;

    public Map<String,EthCandles5m> generateEma5m(){
        QueryWrapper<EthCandles5m> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("candle_time");
        wrapper.last("limit " + records);
        List<EthCandles5m> ethCandles5mList = ethCandles5mService.list(wrapper);
        Map<String,EthCandles5m> resultMap = new HashMap<>();
        for(int i=8;i>0;i--){
            EthCandles5m current = ethCandles5mList.get(i);
            EthCandles5m old = ethCandles5mList.get(i + 1);
            List<Double> data5 = new ArrayList<>();
            data5.add(old.getEma5());
            data5.add(current.getClose());
            current.setEma5(CandleUtil.getEMA(data5,5));
            List<Double> data10 = new ArrayList<>();
            data10.add(old.getEma10());
            data10.add(current.getClose());
            current.setEma10((CandleUtil.getEMA(data10,10)));
        }
        ethCandles5mService.saveOrUpdateBatch(ethCandles5mList);
        resultMap.put("old",ethCandles5mList.get(2));
        resultMap.put("new",ethCandles5mList.get(1));
        return resultMap;
    }

    public EthCandles1h generateEma1h(String candleTime){
        QueryWrapper<EthCandles1h> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("candle_time");
        wrapper.last("limit " + records);
        List<EthCandles1h> ethCandles1hList = ethCandles1hService.list(wrapper);
        int index = 0;
        for(int i=8;i>=0;i--){
            EthCandles1h current = ethCandles1hList.get(i);
            EthCandles1h old = ethCandles1hList.get(i + 1);
            List<Double> data5 = new ArrayList<>();
            data5.add(old.getEma5());
            data5.add(current.getClose());
            current.setEma5(CandleUtil.getEMA(data5,5));
            List<Double> data10 = new ArrayList<>();
            data10.add(old.getEma10());
            data10.add(current.getClose());
            current.setEma10((CandleUtil.getEMA(data10,10)));
            if(current.getCandleTime() == candleTime){
                index = i;
            }
        }
        ethCandles1hService.saveOrUpdateBatch(ethCandles1hList);
        return ethCandles1hList.get(index);
    }

    public EthCandles4h generateEma4h(String candleTime){
        QueryWrapper<EthCandles4h> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("candle_time");
        wrapper.last("limit " + records);
        List<EthCandles4h> ethCandles4hList = ethCandles4hService.list(wrapper);
        int index = 0;
        for(int i=8;i>=0;i--){
            EthCandles4h current = ethCandles4hList.get(i);
            EthCandles4h old = ethCandles4hList.get(i + 1);
            List<Double> data5 = new ArrayList<>();
            data5.add(old.getEma5());
            data5.add(current.getClose());
            current.setEma5(CandleUtil.getEMA(data5,5));
            List<Double> data10 = new ArrayList<>();
            data10.add(old.getEma10());
            data10.add(current.getClose());
            current.setEma10((CandleUtil.getEMA(data10,10)));
            if(current.getCandleTime() == candleTime){
                index = i;
            }
        }
        ethCandles4hService.saveOrUpdateBatch(ethCandles4hList);
        return ethCandles4hList.get(index);
    }


}
