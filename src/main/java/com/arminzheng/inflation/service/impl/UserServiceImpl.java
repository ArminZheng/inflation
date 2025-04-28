package com.arminzheng.inflation.service.impl;

import com.arminzheng.inflation.converter.UserConverter;
import com.arminzheng.inflation.dto.UserDTO;
import com.arminzheng.inflation.model.User;
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

    @Override
    public List<UserDTO> allUsers() {
        List<User> all = userRepository.findAll();
        return userConverter.userToUserDTO(all);
    }
}
