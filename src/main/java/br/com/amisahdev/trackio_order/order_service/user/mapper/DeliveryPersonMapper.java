package br.com.amisahdev.trackio_order.order_service.user.mapper;


import br.com.amisahdev.trackio_order.order_service.user.dto.request.DeliveryPersonRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.DeliveryPersonResponse;
import br.com.amisahdev.trackio_order.order_service.user.models.DeliveryPerson;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface DeliveryPersonMapper {
    @Mapping(target = "userId", ignore = true)
    DeliveryPerson toEntity(DeliveryPersonRequest request);
    DeliveryPersonResponse toResponse(DeliveryPerson entity);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(
            DeliveryPersonRequest request,
            @MappingTarget DeliveryPerson entity
    );
}
