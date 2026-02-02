package br.com.amisahdev.trackio_order.order_service.security.context;

import br.com.amisahdev.trackio_order.order_service.user.dto.UserKeycloakDto;
import br.com.amisahdev.trackio_order.order_service.user.models.User;
import br.com.amisahdev.trackio_order.order_service.user.service.imp.UserServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@RequiredArgsConstructor
public class UserContext {

    private final UserServiceImp userServiceImp;

    private User user;

    public AuthenticatedUser auth() {
        return SecurityUtils.currentUser();
    }

    public User getUser() {
        if (user == null) {
            AuthenticatedUser auth = auth();

            if (auth == null) {
                throw new IllegalStateException("No authenticated user in context");
            }

            user = userServiceImp.findByKeycloakUserId(auth.keycloakUserId())
                    .orElseThrow(() ->
                        new AccessDeniedException(
                                "User not provisioned for keycloakId="
                                        + auth.keycloakUserId()
                        )
                );
        }
        return user;
    }
}
