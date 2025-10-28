package br.com.amisahdev.trackio_order.order_service.order.model;

import br.com.amisahdev.trackio_order.order_service.geral.models.TimeStamp;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.models.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "order")
@Getter
@Setter
public class Order extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Company company;
    @ManyToOne
    private Customer customer;
    private LocalDateTime orderDate;
    private BigDecimal orderAmount;

}
