package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.product.ProductItemDTO;
import org.example.dto.product.ProductPostDTO;
import org.example.entites.CategoryEntity;
import org.example.entites.ProductEntity;
import org.example.entites.ProductImageEntity;
import org.example.mapper.ProductMapper;
import org.example.repository.IProductImageRepository;
import org.example.repository.IProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductService {

    private IProductRepository productRepository;
    private FileService fileService;
    private IProductImageRepository productImageRepository;
    private ProductMapper productMapper;

    public List<ProductItemDTO> getAllProducts() {
        var list = productRepository.findAll();
        return productMapper.toDto(list);
    }

    public ProductItemDTO getProductById(Integer id) {
        return productMapper.toDto(productRepository.findById(id).orElse(null));
    }

    public ProductEntity createProduct(ProductPostDTO product) {
        var entity = new ProductEntity();
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        entity.setCreationTime(LocalDateTime.now());
        var cat = new CategoryEntity();
        cat.setId(product.getCategoryId());
        entity.setCategory(cat);

        productRepository.save(entity);

        int priority = 1;
        for (var img : product.getImages()) {
            var imageName = fileService.load(img);
            var img1 = new ProductImageEntity();
            img1.setPriority(priority);
            img1.setName(imageName);
            img1.setProduct(entity);
            productImageRepository.save(img1);
            priority++;
        }
        return entity;
    }

    public boolean updateProduct(Integer id, ProductPostDTO product) {
        var entity = productRepository.findById(id).orElseThrow();

        // Оновлення основних даних продукту
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());

        var cat = new CategoryEntity();
        cat.setId(product.getCategoryId());
        entity.setCategory(cat);

        // Отримуємо список старих зображень у базі
        Map<String, ProductImageEntity> existingImages = entity.getImages().stream()
                .collect(Collectors.toMap(ProductImageEntity::getName, img -> img));

        List<ProductImageEntity> updatedImages = new ArrayList<>();

        for (int i = 0; i < product.getImages().size(); i++) {
            var img = product.getImages().get(i);

            if ("old-image".equals(img.getContentType())) {
                // Оновлення пріоритету старого зображення
                var imageName = img.getOriginalFilename();
                if (existingImages.containsKey(imageName)) {
                    var oldImage = existingImages.get(imageName);
                    oldImage.setPriority(i);
                    updatedImages.add(oldImage);
                }
            } else {
                // Додавання нового зображення
                var imageName = fileService.load(img);
                var newImage = new ProductImageEntity();
                newImage.setName(imageName);
                newImage.setPriority(i);
                newImage.setProduct(entity);
                updatedImages.add(newImage);
            }
        }

        // Видалення зображень, яких немає в оновленому списку
        for (var img : entity.getImages()) {
            if (!updatedImages.contains(img)) {
                fileService.remove(img.getName());
                productImageRepository.delete(img);
            }
        }

        // Оновлення списку у продукті
        entity.getImages().clear();
        entity.getImages().addAll(updatedImages);

        // Збереження змін
        productImageRepository.saveAll(updatedImages);
        productRepository.save(entity);
        return true;
    }

    public boolean deleteProduct(Integer id) {
        var res = productRepository.findById(id);
        if (res.isEmpty()) {
            return false;
        }
        var imgs = res.get().getImages();
        for (var item : imgs)
        {
            fileService.remove(item.getName());
            productImageRepository.delete(item);
        }
        productRepository.deleteById(id);
        return true;
    }
}