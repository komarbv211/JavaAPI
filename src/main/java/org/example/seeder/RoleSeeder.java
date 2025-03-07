package org.example.seeder;

import lombok.AllArgsConstructor;
import org.example.entites.RoleEntity;
import org.example.repository.IRoleRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class RoleSeeder {

    private IRoleRepository roleRepository;

    public void seed() {
        if (roleRepository.count() == 0) {
            RoleEntity adminRole = new RoleEntity(null, "ADMIN");
            RoleEntity userRole = new RoleEntity(null, "USER");
            roleRepository.saveAll(List.of(adminRole, userRole));
            System.out.println("Roles seeded");
        }
    }
}