package br.com.amisahdev.trackio_order.order_service.product.service.interf;

import br.com.amisahdev.trackio_order.order_service.product.dto.request.CategoryRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.CategoryResponse;

import java.util.List;

public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse update(Long id, CategoryRequest request);
    void delete(Long id);
    CategoryResponse findById(Long id);
    List<CategoryResponse> findAllByCompany_Id(Long id);
}
