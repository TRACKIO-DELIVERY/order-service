package br.com.amisahdev.trackio_order.order_service.product.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponse {
    private Long id;
    private String name;
    private Long companyId;
    private String companyBusinessName;
}
