package com.think.quantstarter.dataCollect.manager;

import com.alibaba.fastjson.JSONArray;

/**
 * @author mpthink
 * @date 2020/9/7 22:00
 */
public interface ICandlesService {
    JSONArray getCandles(String start, String end, String instrument_id, String granularity);
}
