package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.config.security.JwtService;
import org.example.dto.user.*;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping(path="/register",consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> register(@ModelAttribute  UserRegisterDto dto) {
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
}
