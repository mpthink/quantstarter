package com.think.quantstarter.websocket;

import com.alibaba.fastjson.JSONArray;
import com.think.quantstarter.utils.DateUtils;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author mpthink
 * @date 2020/8/10 9:25
 */
@Slf4j
@NoArgsConstructor
public class WebSocketClient {
    private static WebSocket webSocket = null;

    public static WebSocket connection(final String url) {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .build();

        webSocket = client.newWebSocket(request, new OkexWebSocketListener());
        return webSocket;
    }

    //断开连接
    public static void closeConnection() {
        if (null != webSocket) {
            webSocket.close(1000, "User actively closes the connection");
        } else {
            log.info("Please establish the connection before you operate it！");
        }
    }

    //订阅，参数为频道组成的集合
    public static void subscribe(List<String> list) {
        String s = listToJson(list);
        String str = "{\"op\": \"subscribe\", \"args\":" + s + "}";
        if (null != webSocket)
            sendMessage(str);
    }

    //取消订阅，参数为频道组成的集合
    public static void unsubscribe(List<String> list) {
        String s = listToJson(list);
        String str = "{\"op\": \"unsubscribe\", \"args\":" + s + "}";
        if (null != webSocket)
            sendMessage(str);
    }

    private static String listToJson(List<String> list) {
        JSONArray jsonArray = new JSONArray();
        for (String s : list) {
            jsonArray.add(s);
        }
        return jsonArray.toJSONString();
    }

    public static void sendMessage(String str) {
        if (null != webSocket) {
            try {
                Thread.sleep(1300);
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info(DateFormatUtils.format(new Date(), DateUtils.TIME_STYLE_S4)+"Send a message to the server:" + str);
            webSocket.send(str);
        } else {
            log.info("Please establish the connection before you operate it！");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        WebSocketClient webSocketClient = new WebSocketClient();
        webSocketClient.connection("wss://real.okex.com:8443/ws/v3");
        //添加订阅频道
        ArrayList<String> channel = Lists.newArrayList();
        channel.add("futures/ticker:BTC-USDT-200925");
        //调用订阅方法
        webSocketClient.subscribe(channel);
        Thread.sleep(10000000);

    }

}
