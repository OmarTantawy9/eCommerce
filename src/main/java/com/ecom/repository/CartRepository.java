package com.ecom.repository;

import com.ecom.model.Cart;
import com.ecom.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUserEmail(String loggedInUserEmail);

    List<Cart> findCartsByCartItems_Product(Product savedProduct);
}
