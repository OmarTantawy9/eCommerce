package com.ecom.service;

import com.ecom.exceptions.APIException;
import com.ecom.exceptions.ResourceNotFoundException;
import com.ecom.model.Cart;
import com.ecom.model.CartItem;
import com.ecom.model.Product;
import com.ecom.payload.CartDTO;
import com.ecom.payload.ProductDTO;
import com.ecom.repository.CartItemRepository;
import com.ecom.repository.CartRepository;
import com.ecom.repository.ProductRepository;
import com.ecom.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final ProductRepository productRepository;

    private final AuthUtil authUtil;

    private final CartItemRepository cartItemRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        Cart cart = cartRepository.findByUserEmail(authUtil.getLoggedInUserEmail())
                .orElseThrow(() -> new APIException("Cant find cart for user " + authUtil.getLoggedInUsername()));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        boolean cartItemExistsFlag = cartItemRepository.existsByProduct_ProductIdAndCart_CartId(productId, cart.getCartId());

        if(cartItemExistsFlag) {
            throw new APIException("Product: \"" + product.getProductName() + "\" already exists in the cart");
        }

        if(product.getQuantity() == 0) {
            throw new APIException("Product: \"" + product.getProductName() + "\" is not available");
        }

        if(product.getQuantity() < quantity) {
            throw new APIException("Maximum quantity of this product is " + product.getQuantity());
        }

        CartItem cartItem = CartItem.builder()
                .cart(cart)
                .product(product)
                .quantity(quantity)
                .discount(product.getDiscount())
                .productPrice(product.getSpecialPrice())
                .build();


        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cart.getCartItems().add(cartItem);


        Cart savedCart = cartRepository.save(cart);

        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<CartItem> cartItems = savedCart.getCartItems();

        Stream<ProductDTO> productDTOStream = cartItems.stream()
                .map(ci -> {
                    ProductDTO productDTO = modelMapper.map(ci.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(ci.getQuantity());
                    return productDTO;
                });

        cartDTO.setProductDTOS(productDTOStream.toList());

        return cartDTO;
    }


    @Override
    public List<CartDTO> getAllCarts() {

        List<Cart> carts = cartRepository.findAll();

        if(carts.isEmpty()) {
            throw new APIException("No carts are found");
        }

        List<CartDTO> cartDTOS = carts.stream()
                .map(this::getCartDTO)
                .toList();

        return cartDTOS;
    }

    @Override
    public CartDTO getUserCart() {

        Cart cart = cartRepository.findByUserEmail(authUtil.getLoggedInUserEmail())
                .orElseThrow(() -> new APIException("Cant find cart for user " + authUtil.getLoggedInUsername()));

        if(cart.getCartItems().isEmpty()){
            throw new APIException("Cart is empty");
        }

        return getCartDTO(cart);
    }

    private CartDTO getCartDTO(Cart cart) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);

        List<ProductDTO> productDTOS = cart.getCartItems().stream()
                .map(cartItem -> {
                    ProductDTO productDTO = modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                    productDTO.setQuantity(cartItem.getQuantity());
                    return productDTO;
                })
                .toList();

        cartDTO.setProductDTOS(productDTOS);

        return cartDTO;
    }


    @Transactional
    @Override
    public CartDTO updateCartProductQuantity(Long productId, String operation) {

        Integer quantity = operation.equalsIgnoreCase("add") ? 1 : -1;

        Cart cart = cartRepository.findByUserEmail(authUtil.getLoggedInUserEmail())
                .orElseThrow(() -> new APIException("Cant find cart for user " + authUtil.getLoggedInUsername()));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));

        if(product.getQuantity() == 0){
            throw new APIException("Product: \"" + product.getProductName() + "\" is not available");
        }

        CartItem cartItem = cartItemRepository.findByProduct_ProductIdAndCart_CartId(productId, cart.getCartId())
                .orElseThrow(() -> new APIException("Product: \"" + product.getProductName() + "\" is not in the cart"));


        if(product.getQuantity() < (cartItem.getQuantity() + quantity)) {
            throw new APIException("Maximum quantity of this product is " + product.getQuantity());
        }

        cartItem.setQuantity(cartItem.getQuantity() + quantity);
        cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));


        if(cartItem.getQuantity() == 0){
            cartItemRepository.delete(cartItem);
            cart.getCartItems().remove(cartItem);
        }

        Cart savedCart = cartRepository.save(cart);

        return getCartDTO(cart);
    }


    @Override
    @Transactional
    public ProductDTO deleteProductFromCart(Long productId, Long cartId) {

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));

        CartItem cartItem = cartItemRepository.findByProduct_ProductIdAndCart_CartId(productId, cartId)
                .orElseThrow(() -> new APIException("Cart is empty"));


        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.delete(cartItem);



        return modelMapper.map(cartItem.getProduct(), ProductDTO.class);
    }
//
//    @Override
//    public void updateProductInCarts(Long cartId, Long productId) {
//        Cart cart = cartRepository.findById(cartId)
//                .orElseThrow(() -> new ResourceNotFoundException("Cart", "cartId", cartId));
//
//        Product product = productRepository.findById(productId)
//                .orElseThrow(() -> new ResourceNotFoundException("Product", "productId", productId));
//
//        CartItem cartItem = cartItemRepository.findByProduct_ProductIdAndCart_CartId(productId, cartId);
//
//        if(cartItem == null) {
//            throw new APIException("Product: \"" + product.getProductName() + "\" is not available in the cart");
//        }
//
//        Double cartPrice = cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity());
//
//        cartItem.setProductPrice(product.getSpecialPrice());
//
//        cart.setTotalPrice(cartPrice + (cartItem.getProductPrice() * cartItem.getQuantity()));
//
//        cartItemRepository.save(cartItem);
//        cartRepository.save(cart);
//
//    }
//

}

