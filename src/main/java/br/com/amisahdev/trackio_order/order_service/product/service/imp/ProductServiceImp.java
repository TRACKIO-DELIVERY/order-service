package br.com.amisahdev.trackio_order.order_service.product.service.imp;


import br.com.amisahdev.trackio_order.order_service.product.dto.request.ProductRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.ProductResponse;
import br.com.amisahdev.trackio_order.order_service.product.mapper.ProductMapper;
import br.com.amisahdev.trackio_order.order_service.product.model.Category;
import br.com.amisahdev.trackio_order.order_service.product.model.Product;
import br.com.amisahdev.trackio_order.order_service.product.repository.CategoryRepository;
import br.com.amisahdev.trackio_order.order_service.product.repository.ProductRepository;
import br.com.amisahdev.trackio_order.order_service.product.service.interf.ProductService;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CompanyRepository companyRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ProductResponse create(ProductRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Price Invalid");
        }
        if (request.getStock() == null || request.getStock() <= 0) {
            throw new RuntimeException("Stock invalid");
        }


        Product entity = productMapper.toEntity(request);
        entity.setCompany(company);
        entity.setCategory(category);

        Product saved = productRepository.save(entity);

        return productMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Company company =  companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        productMapper.updateProductFromRequest(request, product);

        product.setCompany(company);
        product.setCategory(category);

        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
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
