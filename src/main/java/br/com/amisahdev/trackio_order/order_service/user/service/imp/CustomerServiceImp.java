package br.com.amisahdev.trackio_order.order_service.user.service.imp;

import br.com.amisahdev.trackio_order.order_service.security.context.AuthenticatedUser;
import br.com.amisahdev.trackio_order.order_service.security.context.UserContext;
import br.com.amisahdev.trackio_order.order_service.services.AmazonS3Service;
import br.com.amisahdev.trackio_order.order_service.user.dto.request.CustomerRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CustomerResponse;
import br.com.amisahdev.trackio_order.order_service.user.mapper.AddressMapper;
import br.com.amisahdev.trackio_order.order_service.user.mapper.CustomerMapper;
import br.com.amisahdev.trackio_order.order_service.user.mapper.UserCommonMapper;
import br.com.amisahdev.trackio_order.order_service.user.models.Customer;
import br.com.amisahdev.trackio_order.order_service.user.models.Role;
import br.com.amisahdev.trackio_order.order_service.user.repository.CustomerRepository;
import br.com.amisahdev.trackio_order.order_service.user.service.interf.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@RequiredArgsConstructor
public class CustomerServiceImp implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final AddressMapper addressMapper;
    private final AmazonS3Service s3Service;
    private final UserServiceImp userService;
    private final UserContext userContext;
    private final UserCommonMapper userCommonMapper;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    @Transactional
    public CustomerResponse create(CustomerRequest request, MultipartFile image) {

        AuthenticatedUser authUser = userContext.auth();

        if (userService.findByKeycloakUserId(authUser.keycloakUserId()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        if(customerRepository.existsByCpf(request.getCpf())){
            throw new RuntimeException("CPF already exists");
        }
        Customer entity = customerMapper.toEntity(request);
        entity.setRole(Role.CUSTOMER);

        userCommonMapper.mapKeycloakUser(entity, authUser);
        String uploadedKey = null;

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadFile(image,"Customer");
                entity.setFileKey(imageUrl);
                uploadedKey = imageUrl;
                String fullUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                        bucketName, region, imageUrl);

                entity.setImageUrl(fullUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image to S3", e);
            }
        }

        try {
            Customer saved = customerRepository.save(entity);
            return customerMapper.toResponse(saved);
        } catch (Exception e) {
            if (uploadedKey != null) {
                s3Service.deleteFile(uploadedKey);
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request, MultipartFile newImage) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(()  -> new RuntimeException("Customer not found"));

        String newFileKey = null;
        String oldFileKey = customer.getFileKey();

        customerMapper.updateEntity(request, customer);

        if (newImage != null && !newImage.isEmpty()) {
            try {
                newFileKey = s3Service.uploadFile(newImage,"Customer");
                customer.setFileKey(newFileKey);
                String fullUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                        bucketName, region, newFileKey);
                customer.setImageUrl(fullUrl);

            } catch (IOException e) {
                throw new RuntimeException("Failed to update image", e);
            }
        }

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

        try {
            Customer updatedCustomer = customerRepository.save(customer);

            if (newFileKey != null && oldFileKey != null) {
                s3Service.deleteFile(oldFileKey);
            }

            return customerMapper.toResponse(updatedCustomer);
        } catch (Exception e) {
            if (newFileKey != null) {
                s3Service.deleteFile(newFileKey);
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(()  -> new RuntimeException("Customer not found"));

        String fileKey = customer.getFileKey();

        customerRepository.deleteById(id);

        if (fileKey != null && !fileKey.isEmpty()) {
            try {
                s3Service.deleteFile(fileKey);
            } catch (Exception e) {
                System.err.println("Error deleting file in S3: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        Customer entity = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return customerMapper.toResponse(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse findByCpf(String cpf) {
        Customer customer = customerRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return customerMapper.toResponse(customer);
    }
}
