package com.arminzheng.inflation.service.impl;

import com.arminzheng.inflation.converter.UserConverter;
import com.arminzheng.inflation.dto.UserDTO;
import com.arminzheng.inflation.model.User;
import com.arminzheng.inflation.repository.UserMapper;
import com.arminzheng.inflation.repository.UserRepository;
import com.arminzheng.inflation.service.UserService;
import jakarta.annotation.Resource;
import java.util.List;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

@Order(-1)
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private UserConverter userConverter;
    @Resource
    private UserRepository userRepository;
    @Resource
    private UserMapper userMapper;

    @Override
    public List<UserDTO> allUsers() {
        List<User> all = userRepository.findAll();
        return userConverter.userToUserDTO(all);
    }

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public List<User> test() {
        return userMapper.test();
    }

    @Override
    public void insert(User user) {
        userMapper.insert(user);
    }
}
