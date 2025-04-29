package com.arminzheng.inflation.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "influxdb")
public class InfluxDBProperties {

    private String url;
    private String username;
    private String password;
    private String database;
    private String retention;

}
