package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.security.JwtService;
import org.example.dto.user.UserAuthDto;
import org.example.dto.user.UserRegisterDto;
import org.example.entites.RoleEntity;
import org.example.entites.UserEntity;
import org.example.entites.UserRoleEntity;
import org.example.repository.IRoleRepository;
import org.example.repository.IUserRepository;
import org.example.repository.IUserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IUserRoleRepository userRoleRepository;
    private final JwtService jwtService;

    // Реєстрація нового користувача
    public void registerUser(UserRegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Користувач з таким ім'ям вже існує");
        }
        var userEntity = new UserEntity();
        userEntity.setUsername(dto.getUsername());
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
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
