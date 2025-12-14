package br.com.amisahdev.trackio_order.order_service.user.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CompanyRequest extends UserRequest{
    @NotBlank(message = "CNPJ is required")
    @Size(min = 14, max = 14,message = "CNPJ must have 14 digits.")
    private String cnpj;

    @NotBlank(message = "bussinesName is required")
    private String bussinessName;

    @NotBlank(message = "imageUrl is required")
    private String imageUrl;
}
