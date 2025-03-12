package org.example.dto.user;

import lombok.Data;

@Data
public class UserCreateDto {
    private String username;
    private String password;
    private String role;
}
