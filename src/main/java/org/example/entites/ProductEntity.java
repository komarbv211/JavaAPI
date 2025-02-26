package org.example.entites;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name="tbl_products")
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_crated")
    private LocalDateTime creationTime;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 40000)
    private String description;

    @Column(nullable = false)
    private Double price;

    @ManyToOne
    @JoinColumn(name="category_id", nullable = false)
    @JsonManagedReference
    private CategoryEntity category;


    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProductImageEntity> images;
}
