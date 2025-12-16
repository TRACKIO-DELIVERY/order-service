package br.com.amisahdev.trackio_order.order_service.order.model;


import br.com.amisahdev.trackio_order.order_service.product.model.Product;
import br.com.amisahdev.trackio_order.order_service.user.models.Customer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "orderItem")
@Getter
@Setter
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    @ManyToOne
    @JoinColumn(name = "product_id_product")
    private Product product;
    private Double quantity;
    private BigDecimal price;
}
