package br.com.amisahdev.trackio_order.order_service.user.service.interf;

import br.com.amisahdev.trackio_order.order_service.user.dto.request.AddressRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.AddressResponse;

public interface AddressService {
    AddressResponse create(AddressRequest request);
    AddressResponse update(Long id,AddressRequest request);
    void delete(Long id);
    AddressResponse findById(Long id);
}
