package br.com.amisahdev.trackio_order.order_service.user.service.imp;

import br.com.amisahdev.trackio_order.order_service.user.dto.request.CustomerRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CustomerResponse;
import br.com.amisahdev.trackio_order.order_service.user.mapper.AddressMapper;
import br.com.amisahdev.trackio_order.order_service.user.mapper.CustomerMapper;
import br.com.amisahdev.trackio_order.order_service.user.models.Customer;
import br.com.amisahdev.trackio_order.order_service.user.repository.CustomerRepository;
import br.com.amisahdev.trackio_order.order_service.user.service.interf.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CustomerServiceImp implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;

    @Override
    public CustomerResponse create(CustomerRequest request) {
        if(customerRepository.existsByCpf(request.getCpf())){
            throw new RuntimeException("CPF already exists");
        }
        Customer entity = customerMapper.toEntity(request);
        Customer saved = customerRepository.save(entity);

        return customerMapper.toResponse(saved);
    }

    @Override
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(()  -> new RuntimeException("Customer not found"));

        customerMapper.updateEntity(request, customer);

        if (request.getAddress() != null) {
            if (customer.getAddress() == null) {
                customer.setAddress(addressMapper.toEntity(request.getAddress()));
            } else {
                addressMapper.updateEntity(
                        request.getAddress(),
                        customer.getAddress()
                );
            }
        }

        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found");
        }
        customerRepository.deleteById(id);
    }

    @Override
    public CustomerResponse findById(Long id) {
        Customer entity = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return customerMapper.toResponse(entity);
    }

    @Override
    public CustomerResponse findByCpf(String cpf) {
        Customer customer = customerRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return customerMapper.toResponse(customer);
    }
}
