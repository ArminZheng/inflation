package com.arminzheng.inflation.mapstruct;

import org.mapstruct.AfterMapping;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public abstract class UserConverter {

    @BeforeMapping
    protected void enrichDTOWithFuelType(User user, @MappingTarget UserDTO userDto) {
        userDto.setShowTitle(user.getTitle() + " 10086"); // 后面会被 Mapping 覆盖
        userDto.setExternal(user.getTitle() + " 10086");
    }

    @AfterMapping
    protected void convertNameToUpperCase(@MappingTarget UserDTO userDto) {
        userDto.setUsername(userDto.getUsername().toUpperCase());
    }

    public static UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

    @Mapping(source = "name", target = "username")
    @Mapping(source = "title", target = "showTitle")
    @Mapping(source = "startDt", target = "employeeStartDt", dateFormat = "M-dd-yyyy HH:mm:ss")
    @Mapping(target = "external", ignore = true)
    public abstract UserDTO userToUserDTO(User user);
}
