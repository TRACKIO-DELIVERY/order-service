package br.com.amisahdev.trackio_order.order_service.user.mapper;

import br.com.amisahdev.trackio_order.order_service.user.dto.UserKeycloakDto;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.models.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User fromKeycloak(UserKeycloakDto dto) {
        return Company.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .keycloakUserId(dto.getKeycloakUserId())
                .cnpj("76616127000100")
                .bussinessName("NEGOCIO FANTASIA")
                .build();
    }
}
