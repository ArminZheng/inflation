package com.arminzheng.inflation.repository;

import com.arminzheng.inflation.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM USERS")
    List<User> findAll();

    User findById(Long id);

    List<User> test();

    void insert(User user);
}
