package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.user.*;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UserItemDto> users = userService.getList();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Error while fetching users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error fetching users: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getById(id));
        } catch (Exception e) {
            log.error("Error fetching user by ID: " + id, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }
    }

    @PostMapping(path = "/{userId}/photo", consumes = MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadPhoto(@ModelAttribute UserPhotoDto dto) {
        try {
            String photoName = userService.updateUserPhoto(dto);
            return ResponseEntity.ok(Map.of("message", "Фото оновлено", "photoName", photoName));
        } catch (Exception e) {
            log.error("Error uploading photo", e);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<?> deletePhoto(@PathVariable Long id) {
        try {
            userService.removeUserPhoto(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error deleting photo", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<?> getUserPhoto(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getPhotoById(id));
        } catch (Exception e) {
            log.error("Error fetching user photo", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Photo not found"));
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "Користувача успішно видалено"));
        } catch (Exception e) {
            log.error("Помилка під час видалення користувача", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

}
