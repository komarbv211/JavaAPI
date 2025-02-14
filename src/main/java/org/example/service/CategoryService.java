package org.example.service;

import org.example.dto.category.CategoryCreateDTO;
import org.example.dto.category.CategoryEditDTO;
import org.example.entites.CategoryEntity;
import org.example.repository.ICategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private ICategoryRepository categoryRepository;

    public List<CategoryEntity> getList() {
        return categoryRepository.findAll();
    }

    public CategoryEntity getById(int id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public CategoryEntity create(CategoryCreateDTO dto) {
        CategoryEntity entity = new CategoryEntity();
        entity.setCreationTime(LocalDateTime.now());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setImage(dto.getImage());
        categoryRepository.save(entity);
        return entity;
    }


    public CategoryEntity edit(CategoryEditDTO dto) {
        CategoryEntity entity = categoryRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            entity.setName(dto.getName());
        }
        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getImage() != null && !dto.getImage().isBlank()) {
            entity.setImage(dto.getImage());
        }

        return categoryRepository.save(entity);
    }

    public void delete(int id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        categoryRepository.delete(entity);
    }
}