package org.example.controller;

import org.example.dto.category.CategoryCreateDTO;
import org.example.dto.category.CategoryEditDTO;
import org.example.dto.category.CategoryItemDTO;
import org.example.entites.CategoryEntity;
import org.example.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public List<CategoryItemDTO> getAllCategories() {
        return categoryService.getList();
    }

    @PostMapping(consumes = MULTIPART_FORM_DATA_VALUE)
    public CategoryEntity create(@ModelAttribute CategoryCreateDTO dto) {
        return categoryService.create(dto);
    }

    @PutMapping(path="/{id}", consumes = MULTIPART_FORM_DATA_VALUE)
    public CategoryEntity edit(@PathVariable int id, @ModelAttribute CategoryEditDTO dto) {
        dto.setId(id);
        return categoryService.edit(dto);
    }

    @GetMapping("/{id}")
    public CategoryItemDTO getById(@PathVariable int id) {
        return categoryService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        categoryService.delete(id);
    }
}
