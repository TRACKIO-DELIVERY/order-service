package br.com.amisahdev.trackio_order.order_service.user.dto;

import br.com.amisahdev.trackio_order.order_service.user.models.Role;
import lombok.*;

import java.util.UUID;

@Value
@Builder
public class UserKeycloakDto {
    UUID keycloakUserId;
    String username;
    String email;
    Role role;
}
