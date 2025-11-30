package com.example.shop.controller.auth;

import com.example.shop.dto.auth.AuthRequestDto;
import com.example.shop.dto.auth.AuthResponseDto;
import com.example.shop.dto.auth.RegisterUserRequestDto;
import com.example.shop.entity.User;
import com.example.shop.security.JwtService;
import com.example.shop.security.UserDetailsImpl;
import com.example.shop.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public AuthResponseDto register(@Valid @RequestBody RegisterUserRequestDto dto) {
        log.info("Запрос регистрации нового пользователя: email={}", dto.getEmail());
        User user = userService.registerUser(dto);
        log.debug("Пользователь зарегистрирован: id={}, email={}", user.getId(), user.getEmail());
        UserDetailsImpl userDetails = UserDetailsImpl.fromUser(user);
        String token = jwtService.generateToken(userDetails);
        return new AuthResponseDto(token);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody AuthRequestDto dto) {
        log.info("Попытка входа пользователя: email={}", dto.getEmail());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );
        log.info("Успешная аутентификация пользователя: email={}", dto.getEmail());
        String token = jwtService.generateToken((UserDetailsImpl) authentication.getPrincipal());
        return new AuthResponseDto(token);
    }
}
