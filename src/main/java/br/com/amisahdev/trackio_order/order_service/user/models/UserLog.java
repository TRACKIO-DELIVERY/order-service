package br.com.amisahdev.trackio_order.order_service.user.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "userLog")
@Getter
@Setter
public class UserLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_user_id")
    private User user;
    private String action;
    @CreationTimestamp
    private LocalDateTime logDate;
}
