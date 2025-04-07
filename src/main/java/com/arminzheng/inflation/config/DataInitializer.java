package com.arminzheng.inflation.config;

import com.arminzheng.inflation.model.User;
import com.arminzheng.inflation.repository.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            Faker faker = new Faker(new Locale("en-US"));
            // check whether empty
            if (userRepository.count() == 0) {
                // generate fake 100 data
                for (int i = 0; i < 100; i++) {
                    User user = new User();
                    user.setName(faker.name().fullName());
                    user.setEmail(faker.internet().emailAddress());
                    userRepository.save(user);
                }
            }
        };
    }
}
