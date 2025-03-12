package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.security.JwtService;
import org.example.dto.user.UserAuthDto;
import org.example.entites.UserEntity;
import org.example.repository.IUserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // Реєстрація нового користувача
    public void registerUser(UserEntity userEntity) {
        if (userRepository.existsByUsername(userEntity.getUsername())) {
            throw new RuntimeException("Користувач з таким ім'ям вже існує");
        }
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userRepository.save(userEntity);
    }

    // Аутентифікація користувача
    public String authenticateUser(UserAuthDto userAuthDto) {
        UserEntity user = userRepository.findByUsername(userAuthDto.getUsername()).orElseThrow(() -> new RuntimeException("User not found"));
        if (passwordEncoder.matches(userAuthDto.getPassword(), user.getPassword())) {
            return jwtService.generateAccessToken(user);
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

}
