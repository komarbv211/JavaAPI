package org.example.seeder;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.example.entites.CategoryEntity;
import org.example.repository.ICategoryRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CategorySeeder {
    private final ICategoryRepository categoryRepository;

    @PostConstruct
    public void seed() {
        if (categoryRepository.count() == 0) {
            List<CategoryEntity> categories = List.of(
                    createCategory("Electronics", "electronics.jpg", "Devices and gadgets"),
                    createCategory("Clothing", "clothing.jpg", "Apparel for men and women"),
                    createCategory("Books", "books.jpg", "Various genres of books"),
                    createCategory("Home & Kitchen", "home_kitchen.jpg", "Household essentials"),
                    createCategory("Toys", "toys.jpg", "Toys and games for kids")
            );
            categoryRepository.saveAll(categories);
        }
    }

    private CategoryEntity createCategory(String name, String image, String description) {
        CategoryEntity category = new CategoryEntity();
        category.setName(name);
        category.setImage(image);
        category.setDescription(description);
        category.setCreationTime(LocalDateTime.now());
        return category;
    }
}