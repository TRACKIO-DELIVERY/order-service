package br.com.amisahdev.trackio_order.order_service.user.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyResponse extends UserResponse {
    private String cnpj;
    private String bussinessName;
    private String imageUrl;
    private AddressResponse address;
}
