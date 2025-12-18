package br.com.amisahdev.trackio_order.order_service.user.service.imp;

import br.com.amisahdev.trackio_order.order_service.user.dto.request.AddressRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.AddressResponse;
import br.com.amisahdev.trackio_order.order_service.user.mapper.AddressMapper;
import br.com.amisahdev.trackio_order.order_service.user.models.Address;
import br.com.amisahdev.trackio_order.order_service.user.repository.AddressRepository;
import br.com.amisahdev.trackio_order.order_service.user.service.interf.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AddressServiceImp implements AddressService {

    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;

    @Override
    public AddressResponse create(AddressRequest request) {
        Address toEntity = addressMapper.toEntity(request);
        Address saved = addressRepository.save(toEntity);

        return addressMapper.toResponse(saved);
    }

    @Override
    public AddressResponse update(Long id, AddressRequest request) {
        Address entity = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        addressMapper.updateEntity(request, entity);

        Address updated = addressRepository.save(entity);
        return addressMapper.toResponse(updated);
    }

    @Override
    public void delete(Long id) {
        if (!addressRepository.existsById(id)) {
            throw new RuntimeException("Address not found");
        }
        addressRepository.deleteById(id);
    }

    @Override
    public AddressResponse findById(Long id) {
        Address entity = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        return addressMapper.toResponse(entity);
    }
}
