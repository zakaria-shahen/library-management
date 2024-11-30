package com.example.librarymanagement.mapper;

import com.example.librarymanagement.dto.UserDto;
import com.example.librarymanagement.model.UserModel;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toUserDto(UserModel book);

    UserModel toUserModel(UserDto bookDto);

    List<UserDto> toUserDto(List<UserModel> bookList);

    List<UserModel> toBookModel(List<UserDto> bookDtoList);

}
