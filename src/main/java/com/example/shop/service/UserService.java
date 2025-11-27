package com.example.shop.service;

import com.example.shop.dto.auth.RegisterUserRequestDto;
import com.example.shop.entity.User;

public interface UserService {

    User registerUser(RegisterUserRequestDto dto);

    User getByEmail(String email);

    User getCurrentUser();
}
