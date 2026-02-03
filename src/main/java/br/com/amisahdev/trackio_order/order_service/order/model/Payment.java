package br.com.amisahdev.trackio_order.order_service.order.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@Setter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id",nullable = false)
    private Order order;
    private String paymentMethod;
    private BigDecimal amount;
    private LocalDateTime paymentDate;

}
