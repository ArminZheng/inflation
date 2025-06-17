package com.arminzheng.bill;

import com.arminzheng.tool.util.BootConfigUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BillApp {
    private static final Logger log = LoggerFactory.getLogger(BillApp.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(BillApp.class, args);
        BootConfigUtil.printBootConfig(run);
        log.info((String) run.getBean("CC"));
    }
}
