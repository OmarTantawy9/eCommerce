package com.ecom.service;


import com.ecom.payload.OrderDTO;
import com.ecom.payload.OrderRequestDTO;

public interface OrderService {
    OrderDTO placeOrder(OrderRequestDTO orderRequestDTO, String paymentMethod);
}
