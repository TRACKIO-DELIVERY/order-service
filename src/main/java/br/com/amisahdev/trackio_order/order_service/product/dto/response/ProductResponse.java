package br.com.amisahdev.trackio_order.order_service.product.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Integer stock;
    private BigDecimal price;
    private String imageUrl;
    private Long companyId;
    private String companyName;
    private Long categoryId;
    private String categoryName;
}
