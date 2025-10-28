package br.com.amisahdev.trackio_order.order_service.product.model;


import br.com.amisahdev.trackio_order.order_service.geral.models.TimeStamp;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_product", nullable = false)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "companyId")
    private Company company;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer quantity;
    private String image_url;

}
