package br.com.amisahdev.trackio_order.order_service.product.dto.request;


import br.com.amisahdev.trackio_order.order_service.user.models.Company;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductRequest {
    @NotNull(message = "companyId is not null")
    private Long companyId;
    @NotNull(message = "categoryId is not null")
    private Long categoryId;
    @NotBlank(message = "name is not blank")
    private String name;
    @NotBlank(message = "description is not blank")
    private String description;
    @NotNull(message = "stock is not null")
    private Integer stock;
    @NotNull(message = "stock is not null")
    private BigDecimal price;
    @NotBlank(message = "imageUrl is not null")
    private String imageUrl;
}
