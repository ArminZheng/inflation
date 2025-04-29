package com.arminzheng.inflation.service;

import com.arminzheng.inflation.dto.UserDTO;
import com.arminzheng.inflation.model.UserPO;
import java.util.List;

public interface UserService {

    List<UserDTO> allUsers();

    UserPO findById(Long id);

    List<UserPO> test();

    void insert(UserPO userPO);
}
