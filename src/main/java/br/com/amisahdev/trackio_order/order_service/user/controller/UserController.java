package br.com.amisahdev.trackio_order.order_service.user.controller;

import br.com.amisahdev.trackio_order.order_service.security.context.AuthenticatedUser;
import br.com.amisahdev.trackio_order.order_service.user.models.User;
import br.com.amisahdev.trackio_order.order_service.user.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<String> getUser(@AuthenticationPrincipal AuthenticatedUser user){
        Optional<User> existsUser = userService.findByKeycloakUserId(user.keycloakUserId());
        if(existsUser.isPresent()){
            return ResponseEntity.ok("Usuário Aqui");
        }
        return ResponseEntity.notFound().build();
    }
}
