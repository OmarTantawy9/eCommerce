package com.ecom.repository;

import com.ecom.model.Category;
import com.ecom.model.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryOrderByPriceAsc(Category category);

//    List<Product> findByProductNameLikeIgnoreCase(String keyword);

    Product findByProductName(String productName);


    Product findByProductNameAndCategory(String productName, Category category);

    Page<Product> findByCategory(Category category, Pageable pageConfig);

    Page<Product> findByProductNameLikeIgnoreCase(String s, Pageable pageConfig);

    boolean existsByProductNameAndCategory(String product, Category category);
}
