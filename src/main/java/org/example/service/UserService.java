package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.security.JwtService;
import org.example.dto.user.UserAuthDto;
import org.example.dto.user.UserCreateDto;
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
    public UserEntity registerUser(UserCreateDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Користувач з таким іменем вже існує!");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        UserEntity user = new UserEntity();
        user.setUsername(dto.getUsername());
        user.setPassword(encodedPassword);
        userRepository.save(user);

        RoleEntity role = roleRepository.findByName(dto.getRole())
                .orElseThrow(() -> new RuntimeException("Роль не знайдена"));

        UserRoleEntity userRole = new UserRoleEntity();
        userRole.setUser(user);
        userRole.setRole(role);
        userRoleRepository.save(userRole);

        return user;
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
