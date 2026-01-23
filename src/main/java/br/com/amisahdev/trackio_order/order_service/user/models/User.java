package br.com.amisahdev.trackio_order.order_service.user.models;

import br.com.amisahdev.trackio_order.order_service.geral.models.TimeStamp;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "\"user\"")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User extends TimeStamp implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(length = 50, nullable = false)
    private String username;
    @Column(length = 50, nullable = false)
    private String email;
    @Column(length = 50, nullable = true)
    private String phone;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(length = 50, nullable = true)
    private String expoPushToken;
    @Column(name = "keycloak_user_id", nullable = false, unique = true)
    private UUID keycloakUserId;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role == null
                ? List.of()
                : List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return null; // No password: handled by Keycloak
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
