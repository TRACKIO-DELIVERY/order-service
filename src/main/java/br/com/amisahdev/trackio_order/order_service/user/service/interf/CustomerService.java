package br.com.amisahdev.trackio_order.order_service.user.service.interf;


import br.com.amisahdev.trackio_order.order_service.user.dto.request.CustomerRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CustomerResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CustomerService {
    CustomerResponse create(CustomerRequest request, MultipartFile image);
    CustomerResponse update(Long id,CustomerRequest request,  MultipartFile newImage);
    void delete(Long id);
    CustomerResponse findById(Long id);
    CustomerResponse findByCpf(String cpf);
}
