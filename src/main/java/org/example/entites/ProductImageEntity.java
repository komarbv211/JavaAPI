package org.example.entites;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tbl_product_images")
public class ProductImageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 255, nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer priority;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonManagedReference
    private ProductEntity product;
}
