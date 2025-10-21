package com.docchain.user.api.model;

import com.docchain.user.domain.model.User;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@Setter
public class UserResponseDto {
    String username;
    String fullName;
    String email;
    OffsetDateTime createdAt;

    public static UserResponseDto from(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail().toString());
        dto.setCreatedAt(user.getCreatedAt().atOffset(ZoneOffset.UTC));
        return dto;
    }
}
