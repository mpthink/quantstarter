package com.think.quantstarter.websocket;

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
        //测试服务器返回的字节
        final String byteString=bytes.toString();
        final String s = OkexUtils.uncompress(bytes.toByteArray());
        log.info(DateFormatUtils.format(new Date(), DateUtils.TIME_STYLE_S4) + " Receive: " + s);
    }


}
