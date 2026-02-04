package br.com.amisahdev.trackio_order.order_service.user.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.br.CNPJ;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class CompanyRequest extends UserRequest{
    @NotBlank(message = "CNPJ is required")
    @CNPJ(message = "CNPJ is required")
    private String cnpj;
    @NotBlank(message = "bussinesName is required")
    private String bussinessName;
    @Valid
    @NotNull(message = "AddressId is required")
    private AddressRequest address;
    private BigDecimal deliveryFee;
}
