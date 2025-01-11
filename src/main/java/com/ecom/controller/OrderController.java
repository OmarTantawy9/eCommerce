package com.ecom.controller;

import com.ecom.payload.OrderDTO;
import com.ecom.payload.OrderRequestDTO;
import com.ecom.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> placeOrder(@RequestBody OrderRequestDTO orderRequestDTO,
                                               @PathVariable String paymentMethod) {
        OrderDTO placedOrder = orderService.placeOrder(orderRequestDTO, paymentMethod);
        return new ResponseEntity<>(placedOrder, HttpStatus.CREATED);
    }


}


