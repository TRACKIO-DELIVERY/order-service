package br.com.amisahdev.trackio_order.order_service.product.service.interf;


import br.com.amisahdev.trackio_order.order_service.product.dto.request.ProductRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    ProductResponse create(ProductRequest request, MultipartFile image);
    ProductResponse update(Long id,ProductRequest request,MultipartFile newImage);
    void delete(Long id);
    List<ProductResponse> findAll();
    ProductResponse findById(Long id);
    List<ProductResponse> findByCategoryId(Long id);
    List<ProductResponse> findByCompanyId(Long id);
    List<ProductResponse> findByCategoryAndCompanyId(Long id, Long companyId);
}
