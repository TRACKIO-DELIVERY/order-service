package br.com.amisahdev.trackio_order.order_service.user.mapper;


import br.com.amisahdev.trackio_order.order_service.user.dto.request.CompanyRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CompanyResponse;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import org.mapstruct.*;

@Mapper(componentModel = "spring",uses = {AddressMapper.class})
public interface CompanyMapper {
    @Mapping(target = "userId", ignore = true)
    Company toEntity(CompanyRequest request);
    @Mapping(target = "address", source = "address")
    CompanyResponse toResponse(Company entity);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "userId", ignore = true)
    void updateEntity(
            CompanyRequest request,
            @MappingTarget Company entity
    );
}
