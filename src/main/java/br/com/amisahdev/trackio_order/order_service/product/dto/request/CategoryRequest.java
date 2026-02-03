package br.com.amisahdev.trackio_order.order_service.product.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryRequest {
    @NotBlank(message = "Name is required")
    private String name;
    @NotNull(message = "company is required")
    private Long companyId;
}
