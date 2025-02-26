package org.example.dto.product;

import lombok.Data;

import java.util.List;

@Data
public class ProductPutDTO extends ProductPostDTO {
    //Список фото, які ми маємо видялатися при Edit
    private List<String> removeImages;
}
