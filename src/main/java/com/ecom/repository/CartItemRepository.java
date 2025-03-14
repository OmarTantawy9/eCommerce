package com.ecom.repository;

import com.ecom.model.CartItem;
import com.ecom.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    boolean existsByProduct_ProductIdAndCart_CartId(Long productId, Long cartId);

    Optional<CartItem> findByProduct_ProductIdAndCart_CartId(Long productId, Long cartId);

    List<CartItem> findCartItemByProduct(Product product);
}
