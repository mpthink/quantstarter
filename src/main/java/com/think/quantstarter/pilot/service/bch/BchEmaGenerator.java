package com.think.quantstarter.pilot.service.bch;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.think.quantstarter.analysis.utils.CandleUtil;
import com.think.quantstarter.dataCollect.entity.BchCandles1h;
import com.think.quantstarter.dataCollect.entity.BchCandles4h;
import com.think.quantstarter.dataCollect.entity.BchCandles5m;
import com.think.quantstarter.dataCollect.service.IBchCandles1hService;
import com.think.quantstarter.dataCollect.service.IBchCandles4hService;
import com.think.quantstarter.dataCollect.service.IBchCandles5mService;
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
public class BchEmaGenerator {

    @Resource
    private IBchCandles5mService bchCandles5mService;
    @Resource
    private IBchCandles1hService bchCandles1hService;
    @Resource
    private IBchCandles4hService bchCandles4hService;

    private static int records  = 10;

    public Map<String,BchCandles5m> generateEma5m(){
        QueryWrapper<BchCandles5m> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("candle_time");
        wrapper.last("limit " + records);
        List<BchCandles5m> bchCandles5mList = bchCandles5mService.list(wrapper);
        Map<String,BchCandles5m> resultMap = new HashMap<>();
        for(int i=8;i>0;i--){
            BchCandles5m current = bchCandles5mList.get(i);
            BchCandles5m old = bchCandles5mList.get(i + 1);
            List<Double> data5 = new ArrayList<>();
            data5.add(old.getEma5());
            data5.add(current.getClose());
            current.setEma5(CandleUtil.getEMA(data5,5));
            List<Double> data10 = new ArrayList<>();
            data10.add(old.getEma10());
            data10.add(current.getClose());
            current.setEma10((CandleUtil.getEMA(data10,10)));
        }
        bchCandles5mService.saveOrUpdateBatch(bchCandles5mList);
        resultMap.put("old",bchCandles5mList.get(2));
        resultMap.put("new",bchCandles5mList.get(1));
        return resultMap;
    }

    public BchCandles1h generateEma1h(String candleTime){
        QueryWrapper<BchCandles1h> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("candle_time");
        wrapper.last("limit " + records);
        List<BchCandles1h> bchCandles1hList = bchCandles1hService.list(wrapper);
        int index = 0;
        for(int i=8;i>=0;i--){
            BchCandles1h current = bchCandles1hList.get(i);
            BchCandles1h old = bchCandles1hList.get(i + 1);
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
        bchCandles1hService.saveOrUpdateBatch(bchCandles1hList);
        return bchCandles1hList.get(index);
    }

    public BchCandles4h generateEma4h(String candleTime){
        QueryWrapper<BchCandles4h> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("candle_time");
        wrapper.last("limit " + records);
        List<BchCandles4h> bchCandles4hList = bchCandles4hService.list(wrapper);
        int index = 0;
        for(int i=8;i>=0;i--){
            BchCandles4h current = bchCandles4hList.get(i);
            BchCandles4h old = bchCandles4hList.get(i + 1);
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
        bchCandles4hService.saveOrUpdateBatch(bchCandles4hList);
        return bchCandles4hList.get(index);
    }


}
