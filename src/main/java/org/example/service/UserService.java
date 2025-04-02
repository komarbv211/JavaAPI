package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.security.JwtService;
import org.example.dto.user.UserAuthDto;
import org.example.dto.user.UserItemDto;
import org.example.dto.user.UserPhotoDto;
import org.example.dto.user.UserRegisterDto;
import org.example.entites.UserEntity;
import org.example.mapper.IUserMapper;
import org.example.repository.IUserRepository;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository userRepository;
    private final IUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final FileService fileService;

    @Value("${google.api.userinfo}")
    private String googleUserInfoUrl;


    public List<UserItemDto> getList() {
        List<UserEntity> users = userRepository.findAllWithRoles();
        users.forEach(user -> Hibernate.initialize(user.getUserRoles()));  // Завантажуємо ролі
        return userMapper.toDto(users);
    }

    public UserItemDto getById(Long id) {
        var entity = userRepository.findByIdWithRoles(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Hibernate.initialize(entity.getUserRoles()); // Завантажуємо ролі
        return userMapper.toDto(entity);
    }

    // Реєстрація нового користувача
    public void registerUser(@NotNull UserRegisterDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Користувач з таким ім'ям вже існує");
        }

        var userEntity = new UserEntity();
        userEntity.setUsername(dto.getUsername());
        userEntity.setPassword(passwordEncoder.encode(dto.getPassword()));
        userEntity.setRegistrationDate(LocalDateTime.now());
        userEntity.setRegistrationMethod("Manual");

        if (dto.getPhoto() != null && !dto.getPhoto().isEmpty()) {
            try {
                String photoPath = fileService.load(dto.getPhoto());
                userEntity.setPhoto(photoPath);
            } catch (Exception e) {
                throw new RuntimeException("Помилка завантаження фото: " + e.getMessage());
            }
        }

        userRepository.save(userEntity);
    }



    // Аутентифікація користувача
    public String authenticateUser(UserAuthDto userEntity) {
        UserEntity foundUser = userRepository.findByUsername(userEntity.getUsername())
                .orElseThrow(() -> new RuntimeException("Користувач не знайдений"));

        if (!passwordEncoder.matches(userEntity.getPassword(), foundUser.getPassword())) {
            throw new RuntimeException("Невірний пароль");
        }

        // Генерація JWT токену
        return jwtService.generateAccessToken(foundUser);
    }

    public String signInGoogle(String access_token) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + access_token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(googleUserInfoUrl, HttpMethod.GET, entity, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> userInfo = mapper.readValue(response.getBody(), new TypeReference<Map<String, String>>() {});
            var userEntity = userRepository.findByUsername(userInfo.get("email"))
                    .orElse(null); // Порожній об'єкт
            if(userEntity == null){
                userEntity=new UserEntity();
                userEntity.setUsername(userInfo.get("email"));
                userEntity.setPassword("");
                userRepository.save(userEntity);
            }
            return jwtService.generateAccessToken(userEntity);
        }
        return  null;
    }

    public String updateUserPhoto(UserPhotoDto dto) {
        Optional<UserEntity> userOptional = userRepository.findById(dto.getUserId());
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Користувач не знайдений");
        }

        UserEntity user = userOptional.get();
        String oldPhoto = user.getPhoto();
        String newPhoto = fileService.replace(oldPhoto, dto.getPhoto());

        user.setPhoto(newPhoto);
        userRepository.save(user);
        return newPhoto;
    }

    public void removeUserPhoto(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Користувач не знайдений");
        }

        UserEntity user = userOptional.get();
        fileService.remove(user.getPhoto());
        user.setPhoto(null);
        userRepository.save(user);
    }
    public UserItemDto getPhotoById(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    UserItemDto dto = new UserItemDto();
                    dto.setId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setPhoto(user.getPhoto());
                    return dto;
                })
                .orElseThrow(() -> new RuntimeException("Користувача не знайдено"));
    }
    public void deleteUser(Long userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Користувач не знайдений");
        }

        UserEntity user = userOptional.get();

        // Видалення фото, якщо воно є
        if (user.getPhoto() != null) {
            fileService.remove(user.getPhoto());
        }

        // Видалення користувача з БД
        userRepository.delete(user);
    }

}