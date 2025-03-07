package org.example.repository;

import org.example.entites.UserRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserRoleRepository extends JpaRepository<UserRoleEntity, Long> {
}