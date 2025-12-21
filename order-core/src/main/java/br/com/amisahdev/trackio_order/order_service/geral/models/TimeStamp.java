package br.com.amisahdev.trackio_order.order_service.geral.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class TimeStamp {

    @CreationTimestamp
    private LocalDateTime createdAt;
    @Column(name = "createdUser", nullable = false, updatable = false)
    private String createdUser = "Admin";

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    @Column(name = "updatedUser", nullable = false)
    private String updatedUser = "Admin";


}
