package br.com.amisahdev.trackio_order.order_service.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryPersonRequest extends UserRequest {
    @NotBlank(message = "CPF is required")
    @Size(min = 11, max = 11,message = "CPF must have 11 digits")
    private String cpf;

    @NotBlank(message = "vehicleType is required")
    private String vehicleType;

    @NotNull(message = "active is required")
    private Boolean active;
}
