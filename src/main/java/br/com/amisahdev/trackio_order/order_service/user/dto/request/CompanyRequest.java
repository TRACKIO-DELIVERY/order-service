package br.com.amisahdev.trackio_order.order_service.user.dto.request;


import jakarta.persistence.Column;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class CompanyRequest extends UserRequest{
    @NotBlank(message = "CNPJ is required")
    @Size(min = 14, max = 14,message = "CNPJ must have 14 digits.")
    private String cnpj;
    @NotBlank(message = "bussinesName is required")
    private String bussinessName;
    @Valid
    @NotNull(message = "AddressId is required")
    private AddressRequest address;
    private BigDecimal deliveryFee;
}
