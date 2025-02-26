package org.example.entites;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "tbl_categories")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="date_created")
    private LocalDateTime creationTime;

    @Column(length = 200, nullable = false)
    private String name;

    @Column(length = 255)
    private String image;

    @Column(length = 40000)
    private String description;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductEntity> products;
}
