package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.UserDto;
import com.example.librarymanagement.exception.NotFoundResourceException;
import com.example.librarymanagement.mapper.UserMapper;
import com.example.librarymanagement.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public @NonNull List<UserDto> findAll() {
        return UserMapper.INSTANCE.toUserDto(userRepository.findAll());
    }

    public @NonNull UserDto findById(long id) {
        return userRepository.findById(id)
                .map(UserMapper.INSTANCE::toUserDto)
                .orElseThrow(NotFoundResourceException::new);
    }

    public @NonNull UserDto create(@NonNull UserDto userDto) {
        var model = UserMapper.INSTANCE.toUserModel(userDto);

        return UserMapper.INSTANCE.toUserDto(userRepository.create(model));
    }

    public @NonNull Optional<UserDto> update(@NonNull UserDto userDto) {
        var model = UserMapper.INSTANCE.toUserModel(userDto);

        int updated = userRepository.update(model);
        if (updated == 1) {
            return Optional.empty();
        }

        return Optional.of(create(userDto));
    }
}
