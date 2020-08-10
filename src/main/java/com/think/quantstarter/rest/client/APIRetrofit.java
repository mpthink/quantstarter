package com.think.quantstarter.rest.client;

import com.think.quantstarter.rest.config.OkexRestAPIConfig;
import org.springframework.stereotype.Component;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import javax.annotation.Resource;

/**
 * API Retrofit
 *
 * @author Tony Tian
 * @version 1.0.0
 * @date 2018/3/8 15:40
 */
@Component
public class APIRetrofit {

    @Resource
    private OkexRestAPIConfig config;
    @Resource
    private APIHttpClient client;

    /**
     * Get a retrofit 2 object.
     */
    public Retrofit retrofit() {
        Retrofit.Builder builder = new Retrofit.Builder();
        builder.client(client.client());
        builder.addConverterFactory(ScalarsConverterFactory.create());
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.addCallAdapterFactory(RxJavaCallAdapterFactory.create());
        builder.baseUrl(config.getEndpoint());
        return builder.build();
    }
}
