package br.com.amisahdev.trackio_order.order_service.user.dto.response;


import br.com.amisahdev.trackio_order.order_service.user.models.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private String phone;
    private Role role;
    private String expoPushToken;
    private String fullname;
}
