package br.com.amisahdev.trackio_order.order_service.product.service.interf;


import br.com.amisahdev.trackio_order.order_service.product.dto.request.ProductRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request);
    ProductResponse update(Long id,ProductRequest request);
    void delete(Long id);
    List<ProductResponse> findAll();
    ProductResponse findById(Long id);
    List<ProductResponse> findByCategoryId(Long id);
    List<ProductResponse> findByCompanyId(Long id);
    List<ProductResponse> findByCategoryAndCompanyId(Long id, Long companyId);
}
