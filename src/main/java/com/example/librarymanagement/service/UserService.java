package com.example.librarymanagement.service;

import com.example.librarymanagement.dto.UserDto;
import com.example.librarymanagement.exception.NotFoundResourceException;
import com.example.librarymanagement.exception.UserShouldReturnAllBookBeforeCloseAccountException;
import com.example.librarymanagement.mapper.UserMapper;
import com.example.librarymanagement.repository.BorrowingAndReturnRepository;
import com.example.librarymanagement.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BorrowingAndReturnRepository borrowingAndReturnRepository;
    private final PasswordEncoder passwordEncoder;

    public @NonNull List<UserDto> findAll() {
        return UserMapper.INSTANCE.toUserDto(userRepository.findAll());
    }

    public @NonNull UserDto findById(long id) {
        return userRepository.findById(id)
                .map(UserMapper.INSTANCE::toUserDto)
                .orElseThrow(NotFoundResourceException::new);
    }

    public @NonNull UserDto create(@NonNull UserDto userDto) {
        if (!isAdmin() || userDto.getRole() == null) {
            userDto.setRole("PATRON");
        }

        var model = UserMapper.INSTANCE.toUserModel(userDto);
        model.setPassword(passwordEncoder.encode(userDto.getPassword()));

        return UserMapper.INSTANCE.toUserDto(userRepository.create(model));
    }

    public @NonNull Optional<UserDto> update(@NonNull UserDto userDto) {
        var model = UserMapper.INSTANCE.toUserModel(userDto);
        model.setPassword(passwordEncoder.encode(userDto.getPassword()));

        int updated = userRepository.update(model);
        if (updated == 1) {
            return Optional.empty();
        }

        return Optional.of(create(userDto));
    }

    @Transactional
    public void deleteById(long id) {
        var borrowing = borrowingAndReturnRepository.findBorrowingByUserId(id);
        if (borrowing > 0) {
             throw new UserShouldReturnAllBookBeforeCloseAccountException();
        }

        userRepository.deleteById(id);
    }
    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .anyMatch(it -> it.getAuthority().equals("SCOPE_ADMIN"));
    }
}
