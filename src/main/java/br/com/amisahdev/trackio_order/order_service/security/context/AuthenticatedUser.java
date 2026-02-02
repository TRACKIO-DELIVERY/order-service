package br.com.amisahdev.trackio_order.order_service.security.context;

import java.util.UUID;

public record AuthenticatedUser(
    UUID keycloakUserId,
    String username,
    String email,
    String fullname
) {}
