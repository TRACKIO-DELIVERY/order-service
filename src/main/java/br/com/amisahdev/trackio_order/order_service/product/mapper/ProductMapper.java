package br.com.amisahdev.trackio_order.order_service.product.mapper;

import br.com.amisahdev.trackio_order.order_service.product.dto.request.ProductRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.ProductResponse;
import br.com.amisahdev.trackio_order.order_service.product.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company.userId", source = "companyId")
    @Mapping(target = "category.id", source = "categoryId")
    Product toEntity(ProductRequest request);

    @Mapping(target = "companyId", source = "company.userId")
    @Mapping(target = "companyName", source = "company.bussinessName")
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "company", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateProductFromRequest(ProductRequest request, @MappingTarget Product entity);
}
