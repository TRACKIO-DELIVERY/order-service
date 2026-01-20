package br.com.amisahdev.trackio_order.order_service.security.filter;

import br.com.amisahdev.trackio_order.order_service.security.context.AuthenticatedUser;
import br.com.amisahdev.trackio_order.order_service.user.services.UserService;
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
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KongHeaderAuthenticationFilter extends OncePerRequestFilter {

    private final UserService userService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String userId = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");
        String email = request.getHeader("X-Email");

        log.info("Incoming request {} {}", request.getMethod(), request.getRequestURI());
        log.info("X-User-Id     = {}", userId);
        log.info("X-Username    = {}", username);
        log.info("X-Email       = {}", email);

        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            AuthenticatedUser user = new AuthenticatedUser(
                    UUID.fromString(userId),
                    username,
                    email
            );

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_SYSTEM"))
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
