package org.example.dto.user;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserPhotoDto {
    private MultipartFile photo;
    private Long userId;
}
