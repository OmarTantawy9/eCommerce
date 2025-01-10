package com.ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @NotBlank(message = "Product Name must be provided")
    @Size(min = 5, message = "Product Name must be at least 5 characters")
    private String productName;

    private String image;

    @NotBlank(message = "Product Description must be provided")
    @Size(min = 5, message = "Product Description must be at least 5 characters")
    private String productDescription;

    @NotNull
    private Integer quantity;

    @NotNull
    private double price;

    private double discount;

    private double specialPrice;

    // Owning Side
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

//    @ManyToOne
//    @JoinColumn(name = "seller_id")
//    private User seller;


    // Non-Owning Side
//    @OneToMany(
//            mappedBy = "product",
//            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
//            fetch = FetchType.EAGER
//    )
//    private List<CartItem> cartItems;

}
