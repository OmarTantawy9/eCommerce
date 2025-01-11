package com.ecom.service;

import com.ecom.exceptions.APIException;
import com.ecom.exceptions.ResourceNotFoundException;
import com.ecom.model.*;
import com.ecom.payload.OrderDTO;
import com.ecom.payload.OrderItemDTO;
import com.ecom.payload.OrderRequestDTO;
import com.ecom.payload.ProductDTO;
import com.ecom.repository.*;
import com.ecom.util.AuthUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{

    private final OrderRepository orderRepository;

    private final CartRepository cartRepository;

    private final PaymentRepository paymentRepository;

    private final ProductRepository productRepository;

    private final AuthUtil authUtil;

    private final AddressRepository addressRepository;

    private final OrderItemRepository orderItemRepository;

    private final CartService cartService;

    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public OrderDTO placeOrder(OrderRequestDTO orderRequestDTO, String paymentMethod) {

        String userEmail = authUtil.getLoggedInUserEmail();

        Cart cart = cartRepository.findByUserEmail(userEmail)
                .orElseThrow(() -> new APIException("No cart found for user " + authUtil.getLoggedInUsername()));


        if(cart.getCartItems().isEmpty()){
            throw new APIException("Cart is empty");
        }

        Address address = addressRepository.findById(orderRequestDTO.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address", "addressId", orderRequestDTO.getAddressId()));

        Order order = Order.builder()
                .email(userEmail)
                .orderDate(LocalDate.now())
                .totalPrice(cart.getTotalPrice())
                .orderStatus("Order Accepted")
                .shippingAddress(address)
                .build();


        Payment payment = Payment.builder()
                .paymentMethod(paymentMethod)
                .pgPaymentId(orderRequestDTO.getPgPaymentId())
                .pgName(orderRequestDTO.getPgName())
                .pgStatus(orderRequestDTO.getPgStatus())
                .pgResponseMessage(orderRequestDTO.getPgResponseMessage())
                .order(order)
                .build();


        payment = paymentRepository.save(payment);

        order.setPayment(payment);

        List<OrderItem> orderItems = new ArrayList<>();

        cart.getCartItems()
                .forEach(cartItem -> {
                    OrderItem orderItem = OrderItem.builder()
                            .product(cartItem.getProduct())
                            .orderedProductPrice(cartItem.getProductPrice())
                            .quantity(cartItem.getQuantity())
                            .discount(cartItem.getDiscount())
                            .order(order)
                            .build();
                    orderItems.add(orderItem);

                });

        order.setOrderItems(orderItems);

        cart.getCartItems()
                .forEach(cartItem -> {
                    Product product = cartItem.getProduct();
                    product.setQuantity(product.getQuantity() - cartItem.getQuantity());
                    productRepository.save(product);
                    cartService.deleteProductFromCart(product.getProductId(), cart.getCartId());
                });


        Order savedOrder = orderRepository.save(order);

        OrderDTO orderDTO = modelMapper.map(savedOrder, OrderDTO.class);
        savedOrder.getOrderItems().forEach(orderItem -> {
            ProductDTO productDTO = modelMapper.map(orderItem.getProduct(), ProductDTO.class);
            productDTO.setQuantity(orderItem.getQuantity());
            OrderItemDTO orderItemDTO = modelMapper.map(orderItem, OrderItemDTO.class);
            orderItemDTO.setProductDTO(productDTO);
            orderDTO.getOrderItems().add(orderItemDTO);
        });

        cart.getCartItems().clear();

        return orderDTO;
    }
}