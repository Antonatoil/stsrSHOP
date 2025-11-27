package com.example.shop.controller.auth;

import com.example.shop.dto.auth.AuthRequestDto;
import com.example.shop.dto.auth.AuthResponseDto;
import com.example.shop.dto.auth.RegisterUserRequestDto;
import com.example.shop.entity.User;
import com.example.shop.security.JwtService;
import com.example.shop.security.UserDetailsImpl;
import com.example.shop.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
        User user = userService.registerUser(dto);
        UserDetailsImpl userDetails = UserDetailsImpl.fromUser(user);
        String token = jwtService.generateToken(userDetails);
        return new AuthResponseDto(token);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody AuthRequestDto dto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );
        String token = jwtService.generateToken((UserDetailsImpl) authentication.getPrincipal());
        return new AuthResponseDto(token);
    }
}
