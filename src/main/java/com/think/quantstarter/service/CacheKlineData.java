package com.think.quantstarter.service;

import com.alibaba.fastjson.JSONArray;
import com.think.quantstarter.bean.CollectDataBean;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author mpthink
 * @date 2020/8/11 20:08
 */
public class CacheKlineData {

    public static JSONArray CANDLE1MIN = new JSONArray();
    public static JSONArray CANDLE3MIN = new JSONArray();
    public static JSONArray CANDLE5MIN = new JSONArray();
    public static JSONArray CANDLE15MIN = new JSONArray();
    public static JSONArray CANDLE30MIN = new JSONArray();
    public static JSONArray CANDLE1HOUR = new JSONArray();
    public static JSONArray CANDLE2HOUR = new JSONArray();
    public static JSONArray CANDLE4HOUR = new JSONArray();
    public static JSONArray CANDLE1DAY = new JSONArray();
    public static JSONArray CANDLE1WEEK = new JSONArray();

    public static Queue<CollectDataBean> queue = new LinkedList<>();


}
