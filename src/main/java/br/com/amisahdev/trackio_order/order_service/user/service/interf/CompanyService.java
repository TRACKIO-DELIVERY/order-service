package br.com.amisahdev.trackio_order.order_service.user.service.interf;


import br.com.amisahdev.trackio_order.order_service.user.dto.request.CompanyRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CompanyResponse;

public interface CompanyService {
    CompanyResponse create(CompanyRequest request);
    CompanyResponse update(Long id,CompanyRequest request);
    void delete(Long id);
    CompanyResponse findById(Long id);
    CompanyResponse findByCnpj(String cnpj);
}
