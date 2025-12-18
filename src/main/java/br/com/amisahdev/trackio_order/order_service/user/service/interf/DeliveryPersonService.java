package br.com.amisahdev.trackio_order.order_service.user.service.interf;


import br.com.amisahdev.trackio_order.order_service.user.dto.request.DeliveryPersonRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.DeliveryPersonResponse;

public interface DeliveryPersonService {
    DeliveryPersonResponse create(DeliveryPersonRequest request);
    DeliveryPersonResponse update(Long id,DeliveryPersonRequest request);
    void delete(Long id);
    DeliveryPersonResponse findById(Long id);
    DeliveryPersonResponse findByCpf(String cpf);
}
