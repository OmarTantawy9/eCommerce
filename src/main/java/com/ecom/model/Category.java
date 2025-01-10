package com.ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity(name = "Category") //For Object-Relational Mapping (ORM)
@Data   // Lombok Auto-Generated ToString, EqualsAndHashCode, Getter and Setter, and RequiredArgsConstructor
@NoArgsConstructor  // Lombok Auto-Generated NoArgsConstructor
@AllArgsConstructor // Lombok Auto-Generated AllArgsConstructor
@Table(name = "categories") // DB table information
public class Category {

    @Id // Mark categoryId as Primary Key for the Database Table
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Database auto generates the ID according to Identity Column
    private long categoryId;

    @NotBlank(message = "Category name must be provided")
    @Size(min = 5, message = "Category Name must at least be 5 characters")
    private String categoryName;

//    // Non-Owning Side
//    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//    private List<Product> products;

}
