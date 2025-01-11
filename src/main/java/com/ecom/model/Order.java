package com.ecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Email
    @NonNull
    private String email;

    // Non-Owning Side
    @OneToMany(mappedBy = "order", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<OrderItem> orderItems;

    @Temporal(TemporalType.DATE)
    private LocalDate orderDate;

    // Owning Side
    @OneToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    private Double totalPrice;

    private String orderStatus;

    // Owning Side
    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address shippingAddress;

}
