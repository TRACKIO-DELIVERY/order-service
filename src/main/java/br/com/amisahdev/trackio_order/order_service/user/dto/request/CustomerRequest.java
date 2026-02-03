package br.com.amisahdev.trackio_order.order_service.user.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CustomerRequest extends UserRequest {
    @NotBlank(message = "CPF is required")
    @Size(min = 11, max = 11,message = "CPF must have 11 digits.")
    private String cpf;

    @NotNull(message = "dateOfBirth is required")
    @Past
    private LocalDate dateOfBirth;

    @Valid
    @NotNull(message = "AddressId is required")
    private AddressRequest address;
}
