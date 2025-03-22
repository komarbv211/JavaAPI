package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.security.JwtService;
import org.example.dto.user.UserAuthDto;
import org.example.dto.user.UserItemDto;
import org.example.dto.user.UserPhotoDto;
import org.example.dto.user.UserRegisterDto;
import org.example.entites.UserEntity;
import org.example.repository.IUserRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final FileService fileService;

    @Value("${google.api.userinfo}")
    private String googleUserInfoUrl;

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
}