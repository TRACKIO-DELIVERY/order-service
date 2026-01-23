package br.com.amisahdev.trackio_order.order_service.geral.models;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("development")
public class DevAuditEntityListener {

    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof TimeStamp ts) {
            if (ts.getCreatedUser() == null) {
                set(ts, "createdUser", "DEV_USER");
            }
            if (ts.getUpdatedUser() == null) {
                set(ts, "updatedUser", "DEV_USER");
            }
        }
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        if (entity instanceof TimeStamp ts) {
            if (ts.getUpdatedUser() == null) {
                set(ts, "updatedUser", "DEV_USER");
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
