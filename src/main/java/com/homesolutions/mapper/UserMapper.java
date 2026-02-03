package com.homesolutions.mapper;

import com.homesolutions.dto.UserProfileResponse;
import com.homesolutions.entity.User;

public class UserMapper {

    private UserMapper() {
    }

    public static UserProfileResponse toUserProfileResponse(User user) {
        if (user == null) {
            return null;
        }

        return UserProfileResponse.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
