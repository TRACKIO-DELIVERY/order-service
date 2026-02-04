package br.com.amisahdev.trackio_order.order_service.Unit.Category;

import br.com.amisahdev.trackio_order.order_service.geral.exceptions.BusinessException;
import br.com.amisahdev.trackio_order.order_service.geral.exceptions.CategoryAlreadyExistsException;
import br.com.amisahdev.trackio_order.order_service.geral.exceptions.CategoryNotFoundException;
import br.com.amisahdev.trackio_order.order_service.geral.exceptions.UserNotFoundException;
import br.com.amisahdev.trackio_order.order_service.product.dto.request.CategoryRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.CategoryResponse;
import br.com.amisahdev.trackio_order.order_service.product.mapper.CategoryMapper;
import br.com.amisahdev.trackio_order.order_service.product.model.Category;
import br.com.amisahdev.trackio_order.order_service.product.repository.CategoryRepository;
import br.com.amisahdev.trackio_order.order_service.product.service.imp.CategoryServiceImp;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock private CategoryRepository categoryRepository;
    @Mock private CategoryMapper categoryMapper;
    @Mock private CompanyRepository companyRepository;

    @InjectMocks private CategoryServiceImp categoryService;

    private Company createMockCompany() {
        return Company.builder()
                .bussinessName("Trackio Food")
                .build();
    }

    @Test
    @DisplayName("Deve criar categoria com sucesso")
    void create_Success() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Bebidas");
        request.setCompanyId(1L);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(createMockCompany()));
        when(categoryRepository.existsByNameAndCompany_UserId("Bebidas", 1L)).thenReturn(false);

        Category entity = new Category();
        when(categoryMapper.toEntity(request)).thenReturn(entity);
        when(categoryRepository.save(entity)).thenReturn(entity);
        when(categoryMapper.toResponse(entity)).thenReturn(new CategoryResponse());

        CategoryResponse response = categoryService.create(request);

        assertNotNull(response);
        verify(categoryRepository).save(entity);
    }

    @Test
    @DisplayName("Não deve permitir categorias duplicadas para a mesma empresa")
    void create_ShouldThrowIfDuplicateName() {
        CategoryRequest request = new CategoryRequest();
        request.setName("Limpeza");
        request.setCompanyId(1L);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(createMockCompany()));
        when(categoryRepository.existsByNameAndCompany_UserId("Limpeza", 1L)).thenReturn(true);

        BusinessException ex = assertThrows(CategoryAlreadyExistsException.class, () -> categoryService.create(request));
        assertEquals("Category already exists", ex.getMessage());

        verifyNoInteractions(categoryMapper);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve atualizar o nome da categoria com sucesso")
    void update_Success() {
        Long catId = 1L;
        CategoryRequest request = new CategoryRequest();
        request.setName("Alimentos Atualizados");
        request.setCompanyId(10L);

        Category existingCategory = new Category();
        existingCategory.setName("Alimentos");

        when(categoryRepository.findByIdAndCompany_UserId(catId, 10L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.existsByNameAndCompany_UserId(anyString(), anyLong())).thenReturn(false);
        when(categoryRepository.save(any())).thenReturn(existingCategory);

        categoryService.update(catId, request);

        assertEquals("Alimentos Atualizados", existingCategory.getName());
        verify(categoryRepository).save(existingCategory);
    }

    @Test
    @DisplayName("Deve falhar ao deletar categoria inexistente")
    void delete_NotFound_ThrowsException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(CategoryNotFoundException.class, () -> categoryService.delete(1L));
        assertEquals("Category not found", ex.getMessage());
    }

    @Test
    @DisplayName("MUTANTE: Deve garantir erro se a empresa não existir na criação")
    void mutation_Create_CompanyNotFound() {
        CategoryRequest request = new CategoryRequest();
        request.setCompanyId(999L);

        when(companyRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(UserNotFoundException.class, () -> categoryService.create(request));
        assertEquals("Company not found", ex.getMessage());
    }
}