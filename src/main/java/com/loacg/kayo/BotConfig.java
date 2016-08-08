package com.loacg.kayo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Project: kayo
 * Author: Sendya <18x@loacg.com>
 * Time: 8/1/2016 5:11 PM
 */

@Component
@ConfigurationProperties(prefix = "robot.kayo")
public class BotConfig {

    private String token;
    private String name;
    private String api;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }
}
