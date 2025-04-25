package com.arminzheng.inflation.util;


import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * Startup configuration information
 */
@Data
public class BootConfigBean {
    /**
     * Explanation
     */
    private String configExplain;
    /**
     * Configuration name
     */
    private String configName;
    /**
     * Detailed Configuration
     */
    private List<String> detailedParams = new ArrayList<>();

    /**
     * Instantiates a new Boot config bean.
     *
     * @param configName    the config name
     * @param configExplain the config explain
     */
    public BootConfigBean(String configName, String configExplain) {
        this.configName = configName;
        this.configExplain = configExplain;
    }


}
