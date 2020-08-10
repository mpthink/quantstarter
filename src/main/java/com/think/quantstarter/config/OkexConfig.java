package com.think.quantstarter.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author mpthink
 * @date 2020/8/7 16:51
 */
@Component
@Data
@ConfigurationProperties(prefix = "okexconfig")
public class OkexConfig {
    private String serviceUrl;
    private String apiKey;
    private String secretKey;
    private String passphrase;
    private List<String> swapKline;
}
