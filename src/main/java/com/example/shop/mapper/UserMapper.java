package com.example.shop.mapper;

import com.example.shop.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    default String toString(User user) {
        if (user == null) {
            return null;
        }
        return user.getId() + ":" + user.getEmail();
    }
}
