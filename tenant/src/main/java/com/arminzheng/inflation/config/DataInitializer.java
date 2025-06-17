package com.arminzheng.inflation.config;

import com.arminzheng.inflation.model.UserPO;
import com.arminzheng.inflation.repository.UserRepository;
import com.github.javafaker.Faker;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据源系统初始化器 负责在应用启动时初始化数据源系统
 */
@Slf4j
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initUserData(UserRepository userRepository) {
        return args -> {
            Faker faker = new Faker(new Locale("en-US"));
            // check whether empty
            if (userRepository.count() == 0) {
                // generate fake 100 data
                for (int i = 0; i < 100; i++) {
                    UserPO userPO = new UserPO();
                    userPO.setName(faker.name().fullName());
                    userPO.setEmail(faker.internet().emailAddress());
                    userRepository.save(userPO);
                }
            }
        };
    }
}
