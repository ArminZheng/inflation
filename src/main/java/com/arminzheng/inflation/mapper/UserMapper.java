package com.arminzheng.inflation.mapper;

import com.arminzheng.inflation.model.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM USERS")
    List<UserPO> findAll();

    UserPO findById(Long id);

    List<UserPO> test();

    void insert(UserPO userPO);
}
