package br.com.amisahdev.trackio_order.order_service.user.dto.request;


import br.com.amisahdev.trackio_order.order_service.user.dto.validation.RequiresExpoToken;
import br.com.amisahdev.trackio_order.order_service.user.models.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class UserRequest {
    @NotBlank(message = "phone is required")
    private String phone;

    @NotNull(message = "expoPushToken is required", groups = RequiresExpoToken.class)
    private String expoPushToken;
}
