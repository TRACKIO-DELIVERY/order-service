package br.com.amisahdev.trackio_order.order_service.user.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class CustomerResponse extends UserResponse{
    private String cpf;
    private LocalDate dateOfBirth;
    private String image_url;
}
