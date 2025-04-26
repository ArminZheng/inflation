package com.arminzheng.inflation.mapstruct;

import java.time.LocalDateTime;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class TestMapStruct {
    @Test
    public void testI() {
        User user = new User();

        user.setName("Alice");
        user.setTitle("Internal Control Manager");
        user.setAge(30);
        user.setStartDt(new Date());
        user.setEndDt(LocalDateTime.now());

        // 使用 Mapper 进行转换
        UserDTO userDTO = UserConverter.INSTANCE.userToUserDTO(user);

        System.out.println("Name: " + userDTO.getUsername());
        System.out.println("Age: " + userDTO.getAge());
        System.out.println("title: " + userDTO.getShowTitle());
        System.out.println("all: " + userDTO);
    }
}
