package com.arminzheng.inflation.service.impl;

import com.arminzheng.inflation.converter.UserConverter;
import com.arminzheng.inflation.dto.UserDTO;
import com.arminzheng.inflation.model.UserPO;
import com.arminzheng.inflation.mapper.UserMapper;
import com.arminzheng.inflation.repository.UserRepository;
import com.arminzheng.inflation.service.UserService;
import jakarta.annotation.Resource;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Slf4j
@Order(99)
@Service
public class UserService2Impl implements UserService {
    @Resource
    private UserConverter userConverter;
    @Resource
    private UserRepository userRepository;
    @Resource
    private UserMapper userMapper;
    @Override
    public List<UserDTO> allUsers() {
        List<UserPO> all = userRepository.findAll();
        List<UserDTO> userDTOS = userConverter.userToUserDTO(all);
        // convert again.
        List<UserPO> userPOS = userConverter.userDTOToUser(userDTOS);
        log.info("users = {}", userPOS);
        return userConverter.userToUserDTO(userPOS);
    }

    @Override
    public UserPO findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public List<UserPO> test() {
        return userMapper.test();
    }

    @Override
    public void insert(UserPO userPO) {
        userMapper.insert(userPO);
    }
}
