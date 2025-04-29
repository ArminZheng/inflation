package com.arminzheng.inflation.converter;

import com.arminzheng.inflation.dto.UserDTO;
import com.arminzheng.inflation.model.UserPO;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class UserConverter {

    @BeforeMapping
    protected void enrichDTOWithFuelType(UserPO userPO, @MappingTarget UserDTO userDto) {
        userDto.setExternal(userPO.getCreateTime() + " (external)");
    }

    @AfterMapping
    protected void convertNameToUpperCase(@MappingTarget UserDTO userDto) {
        userDto.setUsername(userDto.getUsername().toUpperCase());
    }

    @Mapping(source = "name", target = "username")
    @Mapping(source = "email", target = "workMail")
    @Mapping(source = "createTime", target = "employeeStartDt", dateFormat = "MM-dd-yyyy HH:mm:ss")
    @Mapping(target = "external", ignore = true)
    public abstract UserDTO userToUserDTO(UserPO userPO);

    public abstract List<UserDTO> userToUserDTO(List<UserPO> userPO);

    @Mapping(target = "name", source = "username")
    @Mapping(target = "email", source = "workMail")
    @Mapping(target = "createTime", source = "employeeStartDt", dateFormat = "MM-dd-yyyy HH:mm:ss")
    public abstract UserPO userDTOToUser(UserDTO userDTO);

    public abstract List<UserPO> userDTOToUser(List<UserDTO> userDTOS);

}
