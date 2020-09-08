package com.think.quantstarter.dataCollect.job;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.extension.service.IService;
import com.think.quantstarter.dataCollect.manager.ICandlesService;
import com.think.quantstarter.dataCollect.utils.ConvertToObjectUtil;
import com.think.quantstarter.utils.DateUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.*;

/**
 * @author mpthink
 * @date 2020/9/7 22:29
 */
@Service
@Slf4j
public class InitGetCandlesJob {

    @Resource
    private ICandlesService candlesService;

    private static String START_TIME = "2018-06-06T00:00:00.000Z";
    private static Integer RECORDS_SIZE = 30;

    @SneakyThrows
    public void initGetBTC(String granularity, Class clz, IService service) throws ParseException {
        String instrument_id = "BTC-USD-SWAP";
        Date startTime = DateUtils.parseUTCTime(START_TIME);
        while (startTime.before(new Date())){
            Thread.sleep(500);
            Date endTime = getEndTime(startTime, granularity);
            JSONArray candles = candlesService.getCandles(DateUtils.timeToString(startTime, 8), DateUtils.timeToString(endTime, 8), instrument_id, granularity);
            if(candles == null){
                startTime = endTime;
            }
            if(candles.size() != RECORDS_SIZE){
                continue;
            }else{
                List<Object> objectList = convertJsonArrayToObjects(candles, clz);
                System.out.println(objectList);
                //service.saveBatch(objectList);
                startTime = endTime;
            }
        }
    }

    public static List<Object> convertJsonArrayToObjects(JSONArray jsonArray, Class clz){
        List<Map<String, Object>> mapList = convertJsonArrayToListMap(jsonArray);
        List<Object> objectList = new ArrayList<>();
        mapList.forEach(map->{
            try {
                Object object = ConvertToObjectUtil.mapToObject(map, clz);
                objectList.add(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return objectList;
    }


    public static List<Map<String, Object>> convertJsonArrayToListMap(JSONArray jsonArray){
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Object json : jsonArray) {
            Map<String, Object> map = new HashMap<>();
            JSONArray temp = (JSONArray) json;
            map.put("candleTime",String.valueOf(temp.get(0)));
            map.put("open",Double.valueOf(temp.get(1).toString()));
            map.put("high",Double.valueOf(temp.get(2).toString()));
            map.put("low",Double.valueOf(temp.get(3).toString()));
            map.put("close",Double.valueOf(temp.get(4).toString()));
            map.put("volume",Double.valueOf(temp.get(5).toString()));
            map.put("currencyVolume",Double.valueOf(temp.get(6).toString()));
            mapList.add(map);
        }
        return mapList;
    }


    private static Date getEndTime(Date startTime, String granularity){
        Integer candles = Integer.valueOf(granularity);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(startTime);
        calendar.set(Calendar.MINUTE, (calendar.get(Calendar.MINUTE) + RECORDS_SIZE * candles / 60));
        return calendar.getTime();
    }

    public static void main(String[] args) throws ParseException {

    }

}
