package com.example.shop.service.impl;

import com.example.shop.dto.auth.RegisterUserRequestDto;
import com.example.shop.entity.User;
import com.example.shop.entity.enums.Role;
import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.NotFoundException;
import com.example.shop.repository.UserRepository;
import com.example.shop.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User registerUser(RegisterUserRequestDto dto) {
        log.info("Регистрация нового пользователя: email={}", dto.getEmail());
        if (userRepository.existsByEmail(dto.getEmail())) {
            log.warn("Регистрация отклонена — пользователь с email={} уже существует", dto.getEmail());
            throw new BadRequestException("User with this email already exists");
        }
        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setFullName(dto.getFullName());
        user.setRole(Role.ROLE_USER);
        User saved = userRepository.save(user);
        log.info("Пользователь успешно зарегистрирован: id={}, email={}", saved.getId(), saved.getEmail());
        return saved;
    }

    @Override
    public User getByEmail(String email) {
        log.debug("Поиск пользователя по email={}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Попытка получить текущего пользователя, но Authentication отсутствует или не аутентифицирован");
            throw new NotFoundException("Current user not found");
        }
        String email = authentication.getName();
        log.debug("Получение текущего пользователя по email из SecurityContext: email={}", email);
        return getByEmail(email);
    }
}
