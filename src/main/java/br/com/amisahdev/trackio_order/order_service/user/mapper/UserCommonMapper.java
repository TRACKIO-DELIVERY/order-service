package br.com.amisahdev.trackio_order.order_service.user.mapper;

import br.com.amisahdev.trackio_order.order_service.security.context.AuthenticatedUser;
import br.com.amisahdev.trackio_order.order_service.user.dto.UserKeycloakDto;
import br.com.amisahdev.trackio_order.order_service.user.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserCommonMapper {

    default void mapKeycloakUser(@MappingTarget User target, AuthenticatedUser source) {
        target.setKeycloakUserId(source.keycloakUserId());
        target.setUsername(source.username());
        target.setFullname(source.fullname());
        target.setEmail(source.email());
    }
}
