package br.com.amisahdev.trackio_order.order_service.user.service.imp;

import br.com.amisahdev.trackio_order.order_service.user.dto.request.CompanyRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CompanyResponse;
import br.com.amisahdev.trackio_order.order_service.user.mapper.AddressMapper;
import br.com.amisahdev.trackio_order.order_service.user.mapper.CompanyMapper;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import br.com.amisahdev.trackio_order.order_service.user.service.interf.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyServiceImp implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final AddressMapper addressMapper;

    @Override
    public CompanyResponse create(CompanyRequest request) {
        if (companyRepository.existsByCnpj(request.getCnpj())){
            throw new RuntimeException("CNPJ already exists");
        }
        Company toEntity = companyMapper.toEntity(request);

        Company saved =  companyRepository.save(toEntity);

        return companyMapper.toResponse(saved);
    }

    @Override
    public CompanyResponse update(Long id, CompanyRequest request) {
        Company entity = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        companyMapper.updateEntity(request, entity);

        if (request.getAddress() != null) {
            if (entity.getAddress() == null) {
                entity.setAddress(addressMapper.toEntity(request.getAddress()));
            } else {
                addressMapper.updateEntity(
                        request.getAddress(),
                        entity.getAddress()
                );
            }
        }

        Company updated = companyRepository.save(entity);

        return companyMapper.toResponse(updated);


    }

    @Override
    public void delete(Long id) {
        if (!companyRepository.existsById(id)){
            throw new RuntimeException("Company not found");
        }
        companyRepository.deleteById(id);
    }

    @Override
    public CompanyResponse findById(Long id) {

        Company entity = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        return companyMapper.toResponse(entity);
    }

    @Override
    public CompanyResponse findByCnpj(String cnpj) {
        Company entity = companyRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        return companyMapper.toResponse(entity);
    }


}
