package com.ecom.service;


import com.ecom.payload.CartDTO;
import com.ecom.payload.ProductDTO;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getUserCart();

    CartDTO updateCartProductQuantity(Long productId, String operation);

    ProductDTO deleteProductFromCart(Long productId, Long cartId);

//    void updateProductInCarts(Long cartId, Long productId);
}
