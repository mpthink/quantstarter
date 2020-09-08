package com.think.quantstarter.dataCollect.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

}
