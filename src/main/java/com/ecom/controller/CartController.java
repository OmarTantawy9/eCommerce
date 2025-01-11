package com.ecom.controller;

import com.ecom.payload.CartDTO;
import com.ecom.payload.CartItemDTO;
import com.ecom.payload.ProductDTO;
import com.ecom.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(@PathVariable Long productId, @PathVariable Integer quantity) {
        CartDTO addedCartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(addedCartDTO, HttpStatus.CREATED);
    }

    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts(){
        List<CartDTO> cartDTOS = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTOS, HttpStatus.OK);
    }

    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getUserCart(){
        CartDTO cartDTO = cartService.getUserCart();
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @PutMapping("/carts/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProductQuantity(@PathVariable Long productId,
                                                             @PathVariable String operation) {
        CartDTO cartDTO = cartService.updateCartProductQuantity(productId, operation);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }

    @DeleteMapping("/carts/{cartId}/product/{productId}")
    public ResponseEntity<ProductDTO> deleteProductFromCart(@PathVariable Long cartId,
                                                            @PathVariable Long productId) {
        ProductDTO productDTO = cartService.deleteProductFromCart(productId, cartId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

}
