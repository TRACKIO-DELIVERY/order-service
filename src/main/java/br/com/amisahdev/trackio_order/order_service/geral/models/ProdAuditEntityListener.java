package br.com.amisahdev.trackio_order.order_service.geral.models;

import br.com.amisahdev.trackio_order.order_service.security.context.AuthenticatedUser;
import br.com.amisahdev.trackio_order.order_service.security.context.UserContext;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("production")
public class ProdAuditEntityListener {

    private final String userEmail;

    public ProdAuditEntityListener(UserContext userContext) {
        this.userEmail = userContext.getUser().getEmail();
    }

    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof TimeStamp ts) {
            if (ts.getCreatedUser() == null) {
                set(ts, "createdUser", userEmail);
            }
            if (ts.getUpdatedUser() == null) {
                set(ts, "updatedUser", userEmail);
            }
        }
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        if (entity instanceof TimeStamp ts) {
            if (ts.getUpdatedUser() == null) {
                set(ts, "updatedUser", userEmail);
            }
        }
    }

    private void set(TimeStamp ts, String field, String value) {
        try {
            var f = TimeStamp.class.getDeclaredField(field);
            f.setAccessible(true);
            f.set(ts, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
