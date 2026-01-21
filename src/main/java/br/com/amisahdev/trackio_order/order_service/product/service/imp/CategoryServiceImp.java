package br.com.amisahdev.trackio_order.order_service.product.service.imp;

import br.com.amisahdev.trackio_order.order_service.product.dto.request.CategoryRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.CategoryResponse;
import br.com.amisahdev.trackio_order.order_service.product.mapper.CategoryMapper;
import br.com.amisahdev.trackio_order.order_service.product.model.Category;
import br.com.amisahdev.trackio_order.order_service.product.repository.CategoryRepository;
import br.com.amisahdev.trackio_order.order_service.product.service.interf.CategoryService;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImp implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional
    public CategoryResponse create(CategoryRequest request) {

        companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        if (categoryRepository.existsByNameAndCompany_UserId(
                request.getName(),
                request.getCompanyId())) {
            throw new RuntimeException("Category already exists");
        }


        Category entity = categoryMapper.toEntity(request);
        Category saved = categoryRepository.save(entity);


        return categoryMapper.toResponse(saved);

    }

    @Override
    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Category category = categoryRepository
                .findByIdAndCompany_UserId(id, request.getCompanyId())
                .orElseThrow(() -> new RuntimeException(
                        "Category not found or does not belong to company"));

        if (categoryRepository.existsByNameAndCompany_UserId(
                request.getName(),
                request.getCompanyId())
                && !category.getName().equalsIgnoreCase(request.getName())) {

            throw new RuntimeException("Category already exists");
        }

        category.setName(request.getName());

        Category updated = categoryRepository.save(category);

        return categoryMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        categoryRepository.delete(category);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> findAllByCompany_Id(Long id) {
        return categoryRepository.findAllByCompany_UserId(id)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
    }
}
