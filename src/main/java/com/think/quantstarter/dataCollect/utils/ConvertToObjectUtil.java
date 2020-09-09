package com.think.quantstarter.dataCollect.utils;

import com.alibaba.fastjson.JSONArray;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author mpthink
 * @date 2020/9/7 22:55
 */
public class ConvertToObjectUtil {

    public static <T> T mapToObject(Map<String, Object> map, Class<T> cls) throws Exception {
        Object target=cls.newInstance();
        T t = cls.cast(target);
        Set<String> keySet = map.keySet();
        Iterator<String> keys = keySet.iterator();
        Field[] fields = t.getClass().getDeclaredFields();
        ArrayList<String> fieldNames = new ArrayList<String>();
        for (Field field : fields) {
            String fileName = field.getName();
            fieldNames.add(fileName);
        }
        while (keys.hasNext()) {
            String fieldName = keys.next();
            if (fieldNames.contains(fieldName)) {
                Object value = map.get(fieldName);
                Field field = t.getClass().getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(t, value);
            }
        }
        return t;
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

}
