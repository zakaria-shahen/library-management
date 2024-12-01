package com.example.librarymanagement.mapper;

import com.example.librarymanagement.dto.UserDto;
import com.example.librarymanagement.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "password", ignore = true)
    UserDto toUserDto(UserModel user);

    UserModel toUserModel(UserDto userDto);

    @Mapping(target = "password", ignore = true)
    List<UserDto> toUserDto(List<UserModel> userList);

}
