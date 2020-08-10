package com.think.quantstarter.rest.config;

import com.think.quantstarter.config.OkexConfig;
import com.think.quantstarter.rest.constant.APIConstants;
import com.think.quantstarter.rest.enums.I18nEnum;
import lombok.Data;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author mpthink
 * @date 2020/8/10 20:32
 */
@Component
@Data
public class OkexRestAPIConfig {
    private String apiKey;
    private String secretKey;
    private String passphrase;
    private String endpoint;
    private long connectTimeout;
    private long readTimeout;
    private long writeTimeout;
    private boolean retryOnConnectionFailure;
    private boolean print;
    private I18nEnum i18n;

    @Resource
    private OkexConfig okexConfig;

    @PostConstruct
    public void init() {
        this.apiKey = okexConfig.getApiKey();
        this.secretKey = okexConfig.getSecretKey();
        this.passphrase = okexConfig.getPassphrase();
        this.endpoint = okexConfig.getRestUrl();
        this.connectTimeout = APIConstants.TIMEOUT;
        this.readTimeout = APIConstants.TIMEOUT;
        this.writeTimeout = APIConstants.TIMEOUT;
        this.retryOnConnectionFailure = true;
        this.print = false;
        this.i18n = I18nEnum.ENGLISH;
    }
}
