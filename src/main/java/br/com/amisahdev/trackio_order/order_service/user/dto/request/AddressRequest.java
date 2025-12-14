package br.com.amisahdev.trackio_order.order_service.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AddressRequest {
    @NotBlank(message = "street is required")
    private String street;
    @NotBlank(message = "city is required")
    private String city;
    @NotBlank(message = "state is required")
    private String state;
    @NotBlank(message = "zipCode is required")
    private String zipCode;
    @NotBlank(message = "neighborhood is required")
    private String neighborhood;
    @NotBlank(message = "number is required")
    private Integer number;
}
