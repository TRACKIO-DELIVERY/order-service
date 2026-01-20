package br.com.amisahdev.trackio_order.order_service.security.context;

import br.com.amisahdev.trackio_order.order_service.user.dtos.UserKeycloakDto;
import br.com.amisahdev.trackio_order.order_service.user.models.User;
import br.com.amisahdev.trackio_order.order_service.user.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
public class UserContext {

    private final UserService userService;

    private User user;

    public User getUser() {
        if (user == null) {
            AuthenticatedUser auth = SecurityUtils.currentUser();

            user = userService.findOrCreate(
                    UserKeycloakDto.builder()
                            .keycloakUserId(auth.keycloakUserId())
                            .username(auth.username())
                            .email(auth.email())
                            .build()
            );
        }
        return user;
    }
}
