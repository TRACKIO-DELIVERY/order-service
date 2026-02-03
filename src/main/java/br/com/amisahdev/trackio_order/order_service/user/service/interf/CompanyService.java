package br.com.amisahdev.trackio_order.order_service.user.service.interf;


import br.com.amisahdev.trackio_order.order_service.user.dto.request.CompanyRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CompanyResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CompanyService {
    CompanyResponse create(CompanyRequest request, MultipartFile image);
    CompanyResponse update(Long id,CompanyRequest request, MultipartFile newImage);
    void delete(Long id);
    CompanyResponse findById(Long id);
    CompanyResponse findByCnpj(String cnpj);
}
