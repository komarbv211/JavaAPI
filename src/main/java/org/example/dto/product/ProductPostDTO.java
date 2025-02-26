package org.example.dto.product;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ProductPostDTO {
    private String name;
    private String description;
    private double price;
    private int categoryId;
    private List<MultipartFile> images;
}
