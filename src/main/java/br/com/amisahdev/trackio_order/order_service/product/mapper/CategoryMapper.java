package br.com.amisahdev.trackio_order.order_service.product.mapper;


import br.com.amisahdev.trackio_order.order_service.product.dto.request.CategoryRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.CategoryResponse;
import br.com.amisahdev.trackio_order.order_service.product.model.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    @Mapping(source = "companyId", target = "company.userId")
    Category toEntity(CategoryRequest request);

    @Mapping(source = "company.userId", target = "companyId")
    @Mapping(source = "company.bussinessName", target = "companyBusinessName")
    CategoryResponse toResponse(Category entity);
}
