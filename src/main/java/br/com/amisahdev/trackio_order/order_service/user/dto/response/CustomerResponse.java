package br.com.amisahdev.trackio_order.order_service.user.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;


@Getter
@Setter
public class CustomerResponse extends UserResponse{
    private String cpf;
    private Date dateOfBirth;
    private String imageUrl;
    private AddressResponse address;
}
