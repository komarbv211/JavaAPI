package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.config.security.JwtService;
import org.example.dto.user.*;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    // Реєстрація нового користувача
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDto dto) {
        try {
            //log.info("Отримано запит на реєстрацію: {}", dto);
            userService.registerUser(dto);
            return ResponseEntity.ok(Map.of("message", "Користувач успішно зареєстрований"));
        } catch (Exception e) {
            //log.error("Помилка реєстрації", e);
            return ResponseEntity.badRequest().body(Map.of("error", "Помилка при реєстрації: " + e.getMessage()));
        }
    }

    // AuthController.java

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserAuthDto userEntity) {
        try {
            String token = userService.authenticateUser(userEntity);
            return ResponseEntity.ok(Collections.singletonMap("token", "Bearer " + token));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "Помилка при вході: " + e.getMessage()));
        }
    }

    @PostMapping("/google")
    public ResponseEntity<?> google_login(@RequestBody UserGoogleAuthDto userEntity) {
        try {
            String token = userService.signInGoogle(userEntity.getToken());
            return ResponseEntity.ok(Collections.singletonMap("token", "Bearer " + token));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Collections.singletonMap("error", "Помилка при вході: " + e.getMessage()));
        }
    }
    @PostMapping(path = "/{userId}/photo", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPhoto(@ModelAttribute UserPhotoDto dto) {
        try {
            String photoName = userService.updateUserPhoto(dto);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("message", "Фото оновлено", "photoName", photoName));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // UserController.java
    @DeleteMapping("/{id}/photo")
    public ResponseEntity<?> deletePhoto(@PathVariable Long id) {
        try {
            userService.removeUserPhoto(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/users/{id}/photo")
    public ResponseEntity<UserItemDto> getUserPhoto(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getPhotoById(id));
    }
}
