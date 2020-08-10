package com.think.quantstarter.rest.client;

import com.alibaba.fastjson.JSON;
import com.think.quantstarter.rest.bean.CursorPager;
import com.think.quantstarter.rest.bean.HttpResult;
import com.think.quantstarter.rest.config.OkexRestAPIConfig;
import com.think.quantstarter.rest.constant.APIConstants;
import com.think.quantstarter.rest.enums.HttpHeadersEnum;
import com.think.quantstarter.rest.exception.APIException;
import com.think.quantstarter.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author mpthink
 * @date 2020/8/10 21:01
 */

@Slf4j
@Component
public class APIClient {

    @Resource
    private OkexRestAPIConfig config;

    @Resource
    private APIRetrofit apiRetrofit;

    private Retrofit retrofit;

    @PostConstruct
    private void init(){
        retrofit = apiRetrofit.retrofit();
    }

    /**
     * Initialize the retrofit operation service
     */
    public <T> T createService(final Class<T> service) {
        return this.retrofit.create(service);
    }

    /**
     * Synchronous send request
     */
    //解析
    public <T> T executeSync(final Call<T> call){
        try {

            final Response<T> response = call.execute();
//            System.out.println("response-------------------------"+call.toString());
            //是否打印config配置信息
            if (config.isPrint()) {
                //打印响应信息
                this.printResponse(response);
            }
            //获取状态码
            final int status = response.code();
            //获取错误信息
            final String message = new StringBuilder().append(response.code()).append(" / ").append(response.message()).toString();
            //响应成功
            if (response.isSuccessful()) {
                return response.body();
                ////如果状态码是400,401,429,500中的任意一个，抛出异常
            } else if (APIConstants.resultStatusArray.contains(status)) {
                final HttpResult result = JSON.parseObject(new String(response.errorBody().bytes()), HttpResult.class);
                if(result.getCode() == 0 && result.getMessage() == null){
                    // System.out.println("错误码："+result.getErrorCode()+"\t错误信息"+result.getErrorMessage());
                    // System.out.println(result);
                    throw new APIException(result.getErrorCode(),result.getErrorMessage());
                }else{
                    //System.out.println("错误码："+result.getCode()+"\t错误信息"+result.getMessage());
                    //抛出异常
                    //  System.out.println(result);
                    throw new APIException(result.getCode(), result.getMessage());
                }
            } else {
                throw new APIException(message);
            }
        } catch (final IOException e) {
            throw new APIException("APIClient executeSync exception.", e);
        }
    }

    /**
     * Synchronous send request
     */
    public <T> CursorPager<T> executeSyncCursorPager(final Call<List<T>> call) {
        try {
            final Response<List<T>> response = call.execute();
            System.out.println("输出响应before");
            if (this.config.isPrint()) {
                this.printResponse(response);
            }
            System.out.println("输出响应after");
            final int status = response.code();
            final String message = response.code() + " / " + response.message();
            if (response.isSuccessful()) {
                final Headers headers = response.headers();
                final CursorPager<T> cursorPager = new CursorPager<T>();
                cursorPager.setData(response.body());
                cursorPager.setBefore(headers.get("OK-BEFORE"));
                cursorPager.setAfter(headers.get("OK-AFTER"));
                cursorPager.setLimit(Optional.ofNullable(headers.get("OK-LIMIT")).map(Integer::valueOf).orElse(100));
                return cursorPager;
            }
            if (APIConstants.resultStatusArray.contains(status)) {
                final HttpResult result = JSON.parseObject(new String(response.errorBody().bytes()), HttpResult.class);
                throw new APIException(result.getCode(), result.getMessage());
            }
            throw new APIException(message);
        } catch (final IOException e) {
            System.out.println("异常信息");
            throw new APIException("APIClient executeSync exception.", e);
        }
    }


    //输出响应（请求头，状态码，信息以及ResponseBody）
    private void printResponse(final Response response) {
        final StringBuilder responseInfo = new StringBuilder();
        responseInfo.append("\n\tResponse").append("(").append(DateUtils.timeToString(null, 4)).append("):");
        if (response != null) {
            final String limit = response.headers().get(HttpHeadersEnum.OK_LIMIT.header());
            if (StringUtils.isNotEmpty(limit)) {
                responseInfo.append("\n\t\t").append("Headers: ");
//                responseInfo.append("\n\t\t\t").append(HttpHeadersEnum.OK_BEFORE.header()).append(": ").append(response.headers().get(HttpHeadersEnum.OK_BEFORE.header()));
//                responseInfo.append("\n\t\t\t").append(HttpHeadersEnum.OK_AFTER.header()).append(": ").append(response.headers().get(HttpHeadersEnum.OK_AFTER.header()));
                responseInfo.append("\n\t\t\t").append(HttpHeadersEnum.OK_FROM.header()).append(": ").append(response.headers().get(HttpHeadersEnum.OK_FROM.header()));
                responseInfo.append("\n\t\t\t").append(HttpHeadersEnum.OK_TO.header()).append(": ").append(response.headers().get(HttpHeadersEnum.OK_TO.header()));
                responseInfo.append("\n\t\t\t").append(HttpHeadersEnum.OK_LIMIT.header()).append(": ").append(limit);
            }
            //responseInfo.append("\n\t\t").append("返回数据: ").append(response.toString());
            responseInfo.append("\n\t\t").append("Status: ").append(response.code());
            responseInfo.append("\n\t\t").append("Message: ").append(response.message());
            if(response.body()!=null){
                responseInfo.append("\n\t\t").append("Response Body: ").append(JSON.toJSONString(response.body()));
            }
        } else {
            responseInfo.append("\n\t\t").append("\n\tRequest Error: response is null");
        }
        log.info(responseInfo.toString());
    }

}
