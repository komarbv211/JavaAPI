package org.example.seeder;

import lombok.RequiredArgsConstructor;
import org.example.entites.RoleEntity;
import org.example.entites.UserEntity;
import org.example.entites.UserRoleEntity;
import org.example.repository.IRoleRepository;
import org.example.repository.IUserRepository;
import org.example.repository.IUserRoleRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSeeder {
    private final IRoleRepository roleRepository;
    private final IUserRepository userRepository;
    private final IUserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    public void seed() {
        if (userRepository.count() == 0) {
            UserEntity admin = new UserEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));

            admin = userRepository.save(admin);

            RoleEntity adminRole = roleRepository.findByName("ADMIN").orElseThrow();
            UserRoleEntity userRole = new UserRoleEntity(null, admin, adminRole);
            userRoleRepository.save(userRole);
            System.out.println("Admin user seeded");
        }
    }
}