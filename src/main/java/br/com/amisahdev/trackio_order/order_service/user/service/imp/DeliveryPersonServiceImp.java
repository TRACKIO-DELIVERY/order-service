package br.com.amisahdev.trackio_order.order_service.user.service.imp;

import br.com.amisahdev.trackio_order.order_service.user.dto.request.DeliveryPersonRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.DeliveryPersonResponse;
import br.com.amisahdev.trackio_order.order_service.user.mapper.DeliveryPersonMapper;
import br.com.amisahdev.trackio_order.order_service.user.models.DeliveryPerson;
import br.com.amisahdev.trackio_order.order_service.user.repository.CustomerRepository;
import br.com.amisahdev.trackio_order.order_service.user.repository.DeliveryPersonRepository;
import br.com.amisahdev.trackio_order.order_service.user.service.interf.DeliveryPersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryPersonServiceImp implements DeliveryPersonService {

    private final DeliveryPersonRepository deliveryPersonRepository;
    private final DeliveryPersonMapper deliveryPersonMapper;
    private final CustomerRepository customerRepository;

    @Override
    public DeliveryPersonResponse create(DeliveryPersonRequest request) {
        if (deliveryPersonRepository.existsByCpf(request.getCpf())) {
            throw new RuntimeException("CPF already exists");
        } else if (customerRepository.existsByCpf(request.getCpf())) {
            throw new RuntimeException("CPF already exists");
        }

        if (!request.getRole().equals("DELIVERY")){
            throw new RuntimeException("Role is not DELIVERY");
        }

        DeliveryPerson deliveryPerson = deliveryPersonMapper.toEntity(request);
        DeliveryPerson saved = deliveryPersonRepository.save(deliveryPerson);

        return deliveryPersonMapper.toResponse(saved);
    }

    @Override
    public DeliveryPersonResponse update(Long id, DeliveryPersonRequest request) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));

        deliveryPersonMapper.updateEntity(request,deliveryPerson);

        DeliveryPerson saved = deliveryPersonRepository.save(deliveryPerson);

        return deliveryPersonMapper.toResponse(saved);

    }

    @Override
    public void delete(Long id) {
        deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));

        deliveryPersonRepository.deleteById(id);
    }

    @Override
    public DeliveryPersonResponse findById(Long id) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));

        return deliveryPersonMapper.toResponse(deliveryPerson);
    }

    @Override
    public DeliveryPersonResponse findByCpf(String cpf) {
        DeliveryPerson deliveryPerson = deliveryPersonRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Delivery person not found"));

        return deliveryPersonMapper.toResponse(deliveryPerson);
    }
}
