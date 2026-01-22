package br.com.amisahdev.trackio_order.order_service.user.dto.request;


import br.com.amisahdev.trackio_order.order_service.user.models.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class UserRequest {

    @NotBlank(message = "username is required")
    private String username;

    @NotBlank(message = "password is required")
    private String password;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "phone is required")
    private String phone;

    @NotNull(message = "Status is required")
    private Role role;

    @NotNull(message = "expoPushToken is required")
    private String expoPushToken;
}
