package br.com.amisahdev.trackio_order.order_service.user.service.interf;


import br.com.amisahdev.trackio_order.order_service.user.dto.request.CustomerRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CustomerResponse;

public interface CustomerService {
    CustomerResponse create(CustomerRequest request);
    CustomerResponse update(Long id,CustomerRequest request);
    void delete(Long id);
    CustomerResponse findById(Long id);
    CustomerResponse findByCpf(String cpf);
}
