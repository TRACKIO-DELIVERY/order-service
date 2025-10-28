package br.com.amisahdev.trackio_order.order_service.user.models;

import br.com.amisahdev.trackio_order.order_service.geral.models.TimeStamp;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "\"user\"")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class User extends TimeStamp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(length = 50, nullable = false)
    private String username;
    @Column(length = 50, nullable = false)
    private String password;
    @Column(length = 50, nullable = false,unique = true)
    private String email;
    @Column(length = 50, nullable = false)
    private String phone;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(length = 50, nullable = false)
    private String expoPushToken;

}
