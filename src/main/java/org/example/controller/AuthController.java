package org.example.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.config.security.JwtService;
import org.example.dto.user.UserAuthDto;
import org.example.dto.user.UserRegisterDto;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    // Реєстрація нового користувача
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserRegisterDto dto) {
        try {
            userService.registerUser(dto);
            return ResponseEntity.ok("Користувач успішно зареєстрований");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Помилка при реєстрації: " + e.getMessage());
        }
    }

    // Логін користувача та отримання JWT токену
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserAuthDto userEntity) {
        try {
            // Перевірка, чи існує користувач і чи правильні дані
            String token = userService.authenticateUser(userEntity);
            return ResponseEntity.ok("Bearer " + token);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Помилка при вході: " + e.getMessage());
        }
    }
}
