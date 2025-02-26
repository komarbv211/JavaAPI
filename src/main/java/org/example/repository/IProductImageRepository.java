package org.example.repository;

import org.example.entites.ProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IProductImageRepository extends JpaRepository<ProductImageEntity,Integer> {
    Optional<ProductImageEntity> findByName(String name);
}
