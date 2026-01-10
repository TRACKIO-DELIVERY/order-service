package br.com.amisahdev.trackio_order.order_service.user.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CompanyResponse extends UserResponse {
    private String cnpj;
    private String bussinessName;
    private String imageUrl;
    private AddressResponse address;
    private BigDecimal deliveryFee;
}
