package br.com.amisahdev.trackio_order.order_service.security.filter;

import br.com.amisahdev.trackio_order.order_service.security.context.AuthenticatedUser;
import br.com.amisahdev.trackio_order.order_service.user.models.Role;
import br.com.amisahdev.trackio_order.order_service.user.models.User;
import br.com.amisahdev.trackio_order.order_service.user.service.imp.UserServiceImp;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KongHeaderAuthenticationFilter extends OncePerRequestFilter {

    private final UserServiceImp userServiceImp;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String userId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");
        String email = request.getHeader("X-Email");
        String fullname = request.getHeader("X-Full-Name");

        log.info("Incoming request {} {}", request.getMethod(), request.getRequestURI());
        log.info("X-User-Id     = {}", userId);
        log.info("X-Username    = {}", username);
        log.info("X-Email       = {}", email);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            Optional<User> userOpt =
                    userServiceImp.findByKeycloakUserId(UUID.fromString(userId));

            Role role = userOpt
                    .map(User::getRole)
                    .orElse(Role.SYSTEM);

            AuthenticatedUser user = new AuthenticatedUser(
                    UUID.fromString(userId),
                    username,
                    email,
                    fullname
            );

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.singletonList( new SimpleGrantedAuthority("ROLE_" + role.name()))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
