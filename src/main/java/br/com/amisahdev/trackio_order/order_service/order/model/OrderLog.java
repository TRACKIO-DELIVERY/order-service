package br.com.amisahdev.trackio_order.order_service.order.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "orderLog")
@Getter
@Setter
public class OrderLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Order order;
    private String text;
    @CreationTimestamp
    private LocalDateTime logDate;
}
