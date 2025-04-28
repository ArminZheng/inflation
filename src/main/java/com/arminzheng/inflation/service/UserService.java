package com.arminzheng.inflation.service;

import com.arminzheng.inflation.dto.UserDTO;
import com.arminzheng.inflation.model.User;
import java.util.List;

public interface UserService {

    List<UserDTO> allUsers();

    User findById(Long id);

    List<User> test();

    void insert(User user);
}
