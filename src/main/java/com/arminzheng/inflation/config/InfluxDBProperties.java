package com.arminzheng.inflation.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "influxdb")
public class InfluxDBProperties {

    private String serverURL = "http://127.0.0.1:8086";
    private String username = "root";
    private String password = "root";
    private String database = "history";
    private String retention;

}
