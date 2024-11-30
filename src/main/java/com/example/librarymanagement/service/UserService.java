package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.UserDto;
import com.example.librarymanagement.mapper.UserMapper;
import com.example.librarymanagement.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public @NonNull List<UserDto> findAll() {
        return UserMapper.INSTANCE.toUserDto(userRepository.findAll());
    }
}
