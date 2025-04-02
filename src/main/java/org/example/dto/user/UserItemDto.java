package org.example.dto.user;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserItemDto {
    private Long id;
    private String username;
    private String photo;
    private LocalDateTime registrationDate;
    private String registrationMethod;
    private List<RoleDto> roles;
}