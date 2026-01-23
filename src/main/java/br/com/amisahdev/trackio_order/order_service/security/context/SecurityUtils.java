package br.com.amisahdev.trackio_order.order_service.security.context;

import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static AuthenticatedUser currentUser() {
        return (AuthenticatedUser)
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getPrincipal();
    }
}
