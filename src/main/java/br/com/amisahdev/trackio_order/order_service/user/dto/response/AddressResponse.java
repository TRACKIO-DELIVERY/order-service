package br.com.amisahdev.trackio_order.order_service.user.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressResponse {
    private Long idAddress;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String neighborhood;
    private Integer number;
}
