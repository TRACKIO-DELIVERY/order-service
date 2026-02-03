package br.com.amisahdev.trackio_order.order_service.user.mapper;

import br.com.amisahdev.trackio_order.order_service.user.dto.request.AddressRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.AddressResponse;
import br.com.amisahdev.trackio_order.order_service.user.models.Address;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Mapping(target = "idAddress",ignore = true)
    Address toEntity(AddressRequest request);
    AddressResponse toResponse(Address entity);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "idAddress", ignore = true)
    void updateEntity(
            AddressRequest request,
            @MappingTarget Address entity
    );
}
