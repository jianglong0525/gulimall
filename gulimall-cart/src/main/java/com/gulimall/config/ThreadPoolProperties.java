package com.gulimall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "gulimall.threadpool")
public class ThreadPoolProperties {
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
}
