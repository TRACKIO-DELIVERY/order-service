package br.com.amisahdev.trackio_order.order_service.user.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeliveryPersonResponse extends UserResponse {
    private String cpf;
    private String vehicleType;
    private String image_url;
    private Boolean active;
}
