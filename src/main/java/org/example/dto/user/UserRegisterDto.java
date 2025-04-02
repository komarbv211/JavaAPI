package org.example.dto.user;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserRegisterDto {
    private String username;
    private String password;
    private MultipartFile photo;
    }
