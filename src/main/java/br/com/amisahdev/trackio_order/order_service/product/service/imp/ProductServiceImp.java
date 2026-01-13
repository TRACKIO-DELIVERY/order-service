package br.com.amisahdev.trackio_order.order_service.product.service.imp;


import br.com.amisahdev.trackio_order.order_service.product.dto.request.ProductRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.ProductResponse;
import br.com.amisahdev.trackio_order.order_service.product.mapper.ProductMapper;
import br.com.amisahdev.trackio_order.order_service.product.model.Category;
import br.com.amisahdev.trackio_order.order_service.product.model.Product;
import br.com.amisahdev.trackio_order.order_service.product.repository.CategoryRepository;
import br.com.amisahdev.trackio_order.order_service.product.repository.ProductRepository;
import br.com.amisahdev.trackio_order.order_service.product.service.interf.ProductService;
import br.com.amisahdev.trackio_order.order_service.services.AmazonS3Service;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CompanyRepository companyRepository;
    private final CategoryRepository categoryRepository;
    private final AmazonS3Service s3Service;

    @Value("${spring.cloud.aws.region.static}")
    private String region;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request, MultipartFile image) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));


        Product entity = productMapper.toEntity(request);
        entity.setCompany(company);
        entity.setCategory(category);

        if (image != null && !image.isEmpty()) {
            try {
                String imageUrl = s3Service.uploadFile(image,"Products");
                entity.setFileKey(imageUrl);
                String fullUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                        bucketName, region, imageUrl);

                entity.setImageUrl(fullUrl);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload image to S3", e);
            }
        }

        Product saved = productRepository.save(entity);

        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request,MultipartFile newImage) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Company company =  companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        productMapper.updateProductFromRequest(request, product);

        if (newImage != null && !newImage.isEmpty()) {
            try {
                if (product.getFileKey() != null) {
                    s3Service.deleteFile(product.getFileKey());
                }

                String newFileName = s3Service.uploadFile(newImage,"Products");
                product.setFileKey(newFileName);
                String fullUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",
                        bucketName, region, newFileName);
                product.setImageUrl(fullUrl);

            } catch (IOException e) {
                throw new RuntimeException("Failed to update image", e);
            }
        }

        product.setCompany(company);
        product.setCategory(category);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        String fileKey = product.getFileKey();

        productRepository.deleteById(id);

        if (fileKey != null && !fileKey.isEmpty()) {
            try {
                s3Service.deleteFile(fileKey);
            } catch (Exception e) {
                System.err.println("Erro ao deletar arquivo no S3: " + e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productMapper.toResponseList(productRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findByCategoryId(Long id) {
        categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return productMapper.toResponseList(productRepository.findByCategoryId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findByCompanyId(Long id) {
        companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        return productMapper.toResponseList(productRepository.findByCompanyUserId(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> findByCategoryAndCompanyId(Long id, Long companyId) {
        companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));
        categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return productMapper.toResponseList(productRepository.findByCategoryIdAndCompanyUserId(id, companyId));
    }
}
