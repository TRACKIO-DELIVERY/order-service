package br.com.amisahdev.trackio_order.order_service.user.mapper;


import br.com.amisahdev.trackio_order.order_service.user.dto.request.CustomerRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CustomerResponse;
import br.com.amisahdev.trackio_order.order_service.user.models.Customer;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AddressMapper.class})
public interface CustomerMapper {
    @Mapping(target = "userId",ignore = true)
    Customer toEntity(CustomerRequest request);
    @Mapping(target = "address", source = "address")
    CustomerResponse toResponse(Customer entity);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    void updateEntity(
            CustomerRequest request,
            @MappingTarget Customer entity
    );
}
