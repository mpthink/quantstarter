package com.think.quantstarter.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.think.quantstarter.dataCollect.bean.CollectDataBean;
import com.think.quantstarter.rest.constant.APIConstants;
import com.think.quantstarter.service.CacheKlineData;
import com.think.quantstarter.utils.DateUtils;
import com.think.quantstarter.utils.OkexUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author mpthink
 * @date 2020/8/7 17:02
 */
@Slf4j
public class OkexWebSocketListener extends WebSocketListener {

    private ScheduledExecutorService service;

    @Override
    public void onOpen(final WebSocket webSocket, final Response response) {
        //连接成功后，设置定时器，每隔25s，自动向服务器发送心跳，保持与服务器连接
        log.info(Instant.now().toString() + " Connected to the server success!");
        Runnable runnable = new Runnable() {
            public void run() {
                // task to run goes here
                WebSocketClient.sendMessage("ping");
            }
        };
        service = Executors.newSingleThreadScheduledExecutor();
        // 第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间
        service.scheduleAtFixedRate(runnable, 25, 25, TimeUnit.SECONDS);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        log.info("Connection is about to disconnect！");
        webSocket.close(1000, "Long time no message was sent or received！");
        webSocket = null;
    }

    @Override
    public void onClosed(final WebSocket webSocket, final int code, final String reason) {
        log.info("Connection dropped！");
    }

    @Override
    public void onFailure(final WebSocket webSocket, final Throwable t, final Response response) {
        log.info("Connection failed,Please reconnect! error message: {}", t.getMessage());
        if (Objects.nonNull(service)) {
            service.shutdown();
        }
    }

    @Override
    public void onMessage(final WebSocket webSocket, final ByteString bytes) {
        final String s = OkexUtils.uncompress(bytes.toByteArray());
        JSONObject jsonObject = JSON.parseObject(s);
        String dataType = jsonObject.getString("table");
        switch (dataType){
            case "swap/ticker":
                insertDataToQueue(jsonObject.getJSONArray("data").getString(0));
                break;
            case "swap/candle180s":
                updateLastCacheData(CacheKlineData.CANDLE3MIN, jsonObject.getJSONArray("data").getJSONObject(0).get("candle"));
                break;
            case "swap/candle300s":
                updateLastCacheData(CacheKlineData.CANDLE5MIN, jsonObject.getJSONArray("data").getJSONObject(0).get("candle"));
                break;
            case "swap/candle900s":
                updateLastCacheData(CacheKlineData.CANDLE15MIN, jsonObject.getJSONArray("data").getJSONObject(0).get("candle"));
                break;
            case "swap/candle1800s":
                updateLastCacheData(CacheKlineData.CANDLE30MIN, jsonObject.getJSONArray("data").getJSONObject(0).get("candle"));
                break;
            case "swap/candle3600s":
                updateLastCacheData(CacheKlineData.CANDLE1HOUR, jsonObject.getJSONArray("data").getJSONObject(0).get("candle"));
                break;
            case "swap/candle14400s":
                updateLastCacheData(CacheKlineData.CANDLE4HOUR, jsonObject.getJSONArray("data").getJSONObject(0).get("candle"));
                break;
            case "swap/candle86400s":
                updateLastCacheData(CacheKlineData.CANDLE1DAY, jsonObject.getJSONArray("data").getJSONObject(0).get("candle"));
                break;
            case "swap/candle2678400s":
                updateLastCacheData(CacheKlineData.CANDLE1WEEK, jsonObject.getJSONArray("data").getJSONObject(0).get("candle"));
                break;
        }
        log.info(DateFormatUtils.format(new Date(), DateUtils.TIME_STYLE_S4) + " Receive: " + s);
    }

    private void updateLastCacheData(JSONArray jsonArray, Object target){
        if (jsonArray.size() == APIConstants.KLINES_NUMBERS){
            jsonArray.set(APIConstants.KLINES_NUMBERS-1,target);
        }else{
            log.error("Not update cache last data because json array size mismatch!");
        }
    }

    private void insertDataToQueue(String ticker){
        CollectDataBean bean = CollectDataBean.builder().ticker(ticker)
                .candle3Min(CacheKlineData.CANDLE3MIN.toJSONString())
                .candle5Min(CacheKlineData.CANDLE5MIN.toJSONString())
                .candle15Min(CacheKlineData.CANDLE15MIN.toJSONString())
                .candle30Min(CacheKlineData.CANDLE30MIN.toJSONString())
                .candle1Hour(CacheKlineData.CANDLE1HOUR.toJSONString())
                .candle4Hour(CacheKlineData.CANDLE4HOUR.toJSONString())
                .candle1Day(CacheKlineData.CANDLE1DAY.toJSONString())
                .candle1Week(CacheKlineData.CANDLE1WEEK.toJSONString())
                .build();
        CacheKlineData.queue.add(bean);
    }
}
