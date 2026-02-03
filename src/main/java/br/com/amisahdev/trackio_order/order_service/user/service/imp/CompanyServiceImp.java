package br.com.amisahdev.trackio_order.order_service.user.service.imp;

import br.com.amisahdev.trackio_order.order_service.security.context.AuthenticatedUser;
import br.com.amisahdev.trackio_order.order_service.security.context.UserContext;
import br.com.amisahdev.trackio_order.order_service.services.AmazonS3Service;
import br.com.amisahdev.trackio_order.order_service.user.dto.request.CompanyRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CompanyResponse;
import br.com.amisahdev.trackio_order.order_service.user.mapper.AddressMapper;
import br.com.amisahdev.trackio_order.order_service.user.mapper.CompanyMapper;
import br.com.amisahdev.trackio_order.order_service.user.mapper.UserCommonMapper;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.models.Role;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import br.com.amisahdev.trackio_order.order_service.user.service.interf.CompanyService;
import br.com.amisahdev.trackio_order.order_service.user.service.interf.UserService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CompanyServiceImp implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;
    private final AddressMapper addressMapper;
    private final AmazonS3Service amazonS3Service;
    private final UserContext userContext;
    private final UserService userService;
    private final UserCommonMapper userCommonMapper;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    @Transactional
    public CompanyResponse create(CompanyRequest request, MultipartFile image) {

        AuthenticatedUser authUser = userContext.auth();

        if (userService.findByKeycloakUserId(authUser.keycloakUserId()).isPresent()) {
            throw new RuntimeException("User already exists");
        }
        if (companyRepository.existsByCnpj(request.getCnpj())){
            throw new RuntimeException("CNPJ already exists");
        }

        Company toEntity = companyMapper.toEntity(request);

        toEntity.setRole(Role.COMPANY);
        toEntity.setAddress(addressMapper.toEntity(request.getAddress()));

        userCommonMapper.mapKeycloakUser(toEntity, authUser);
        String uploadedKey = null;

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = amazonS3Service.uploadFile(image,"Company");
                toEntity.setFileKey(imageUrl);
                uploadedKey = imageUrl;
                String fullUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                        bucketName, region, imageUrl);

                toEntity.setImageUrl(fullUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image to S3", e);
            }
        }

        try {
            Company saved =  companyRepository.save(toEntity);
            return companyMapper.toResponse(saved);
        } catch (Exception e) {
            if (uploadedKey != null){
                amazonS3Service.deleteFile(uploadedKey);
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public CompanyResponse update(Long id, CompanyRequest request, MultipartFile newImage) {
        Company entity = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        String newFileKey = null;
        String oldFileKey = entity.getFileKey();

        companyMapper.updateEntity(request, entity);

        if (newImage != null && !newImage.isEmpty()) {
            try {
                newFileKey = amazonS3Service.uploadFile(newImage,"Company");
                entity.setFileKey(newFileKey);
                String fullUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                        bucketName, region, newFileKey);
                entity.setImageUrl(fullUrl);

            } catch (IOException e) {
                throw new RuntimeException("Failed to update image", e);
            }
        }

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

        try {
            Company updated = companyRepository.save(entity);

            if (newFileKey != null && oldFileKey != null){
                amazonS3Service.deleteFile(oldFileKey);
            }

            return companyMapper.toResponse(updated);
        } catch (Exception e) {
            if (newFileKey != null){
                amazonS3Service.deleteFile(newFileKey);
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(()  -> new RuntimeException("Company not found"));

        String fileKey = company.getFileKey();
        companyRepository.deleteById(id);

        if (fileKey != null && !fileKey.isEmpty()) {
            try {
                amazonS3Service.deleteFile(fileKey);
            } catch (Exception e) {
                System.err.println("Error deleting file in S3: " + e.getMessage());
            }

        }


    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse findById(Long id) {

        Company entity = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        return companyMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public CompanyResponse findByCnpj(String cnpj) {
        Company entity = companyRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        return companyMapper.toResponse(entity);
    }


}
