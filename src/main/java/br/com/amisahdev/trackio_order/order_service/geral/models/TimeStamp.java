package br.com.amisahdev.trackio_order.order_service.geral.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(DevAuditEntityListener.class)
public abstract class TimeStamp {

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "createdUser", nullable = false, updatable = false)
    private String createdUser;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "updatedUser", nullable = false)
    private String updatedUser;


}
