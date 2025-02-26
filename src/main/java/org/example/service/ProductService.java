package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.product.ProductItemDTO;
import org.example.dto.product.ProductPostDTO;
import org.example.dto.product.ProductPutDTO;
import org.example.entites.CategoryEntity;
import org.example.entites.ProductEntity;
import org.example.entites.ProductImageEntity;
import org.example.mapper.ProductMapper;
import org.example.repository.IProductImageRepository;
import org.example.repository.IProductRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

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

    public boolean updateProduct(Integer id, ProductPutDTO product) {
        var res = productRepository.findById(id);
        if (res.isEmpty()) {
            return false;
        }
        var entity = res.get();
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        entity.setPrice(product.getPrice());
        var cat = new CategoryEntity();
        cat.setId(product.getCategoryId());
        entity.setCategory(cat);
        productRepository.save(entity);

        for (var img : product.getRemoveImages()) {
            var removeImage = productImageRepository.findByName(img).get();
            fileService.remove(img);
            productImageRepository.delete(removeImage);
        }

        int priority = 1;
        for (var img : product.getImages()) {
            ProductImageEntity image = new ProductImageEntity();
            var imageName = fileService.load(img);
            image.setName(imageName);
            image.setPriority(priority);
            image.setProduct(entity);
            productImageRepository.save(image);
            priority++;
        }

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