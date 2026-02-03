package br.com.amisahdev.trackio_order.order_service.Unit.Product;

import br.com.amisahdev.trackio_order.order_service.product.dto.request.ProductRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.ProductResponse;
import br.com.amisahdev.trackio_order.order_service.product.mapper.ProductMapper;
import br.com.amisahdev.trackio_order.order_service.product.model.Category;
import br.com.amisahdev.trackio_order.order_service.product.model.Product;
import br.com.amisahdev.trackio_order.order_service.product.repository.CategoryRepository;
import br.com.amisahdev.trackio_order.order_service.product.repository.ProductRepository;
import br.com.amisahdev.trackio_order.order_service.product.service.imp.ProductServiceImp;
import br.com.amisahdev.trackio_order.order_service.user.models.Company;
import br.com.amisahdev.trackio_order.order_service.user.repository.CompanyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ProductMapper productMapper;

    @InjectMocks private ProductServiceImp productService;

    @Test
    @DisplayName("Deve criar produto garantindo que o ID da Company (User) seja respeitado")
    void create_ShouldUseUserIdAsCompanyId() {

        Long inputId = 10L;

        Company mockCompany = new Company();
        mockCompany.setUserId(inputId);

        Category mockCategory = new Category();
        mockCategory.setId(1L);

        ProductRequest request = new ProductRequest();
        request.setCompanyId(inputId);
        request.setCategoryId(1L);
        request.setName("Produto Teste");
        request.setPrice(new BigDecimal("100.00"));
        request.setStock(10);

        Product entity = new Product();

        when(companyRepository.findById(inputId)).thenReturn(Optional.of(mockCompany));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(mockCategory));
        when(productMapper.toEntity(any())).thenReturn(entity);
        when(productRepository.save(any())).thenReturn(entity);
        when(productMapper.toResponse(any())).thenReturn(new ProductResponse());


        productService.create(request);

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(captor.capture());

        Product savedProduct = captor.getValue();
        assertNotNull(savedProduct.getCompany());
        assertEquals(inputId, savedProduct.getCompany().getUserId());
    }

    @Test
    @DisplayName("Não deve permitir criar produto com preço zero ou negativo")
    void create_ShouldThrowExceptionForInvalidPrice() {

        ProductRequest request = new ProductRequest();
        request.setPrice(BigDecimal.ZERO);
        request.setCompanyId(10L);
        request.setCategoryId(1L);


        when(companyRepository.findById(10L)).thenReturn(Optional.of(new Company()));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));

        assertThrows(RuntimeException.class, () -> productService.create(request));
    }

    @Test
    @DisplayName("Não deve permitir criar produto com estoque zerado ou negativo")
    void create_ShouldThrowExceptionForInvalidStock() {

        ProductRequest request = new ProductRequest();
        request.setPrice(new BigDecimal("100.00"));
        request.setStock(0);
        request.setCompanyId(10L);
        request.setCategoryId(1L);

        when(companyRepository.findById(10L)).thenReturn(Optional.of(new Company()));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));


        assertThrows(RuntimeException.class, () -> productService.create(request));

        request.setStock(-5);
        assertThrows(RuntimeException.class, () -> productService.create(request));
    }

    @Test
    @DisplayName("Deve buscar produtos por ID com sucesso")
    void findById_Success() {
        Long id = 1L;
        Product product = new Product();
        product.setId(id);

        when(productRepository.findById(id)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(new ProductResponse());

        ProductResponse response = productService.findById(id);

        assertNotNull(response);
        verify(productRepository).findById(id);
    }

    @Test
    @DisplayName("Deve buscar produtos por categoria e empresa com sucesso")
    void findByCategoryAndCompanyId_Success() {
        Long catId = 1L, companyId = 10L;

        when(companyRepository.findById(companyId)).thenReturn(Optional.of(new Company()));
        when(categoryRepository.findById(catId)).thenReturn(Optional.of(new Category()));
        when(productRepository.findByCategoryIdAndCompanyUserId(catId, companyId)).thenReturn(List.of(new Product()));
        when(productMapper.toResponseList(any())).thenReturn(List.of(new ProductResponse()));

        List<ProductResponse> result = productService.findByCategoryAndCompanyId(catId, companyId);

        assertFalse(result.isEmpty());
        verify(productRepository).findByCategoryIdAndCompanyUserId(catId, companyId);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar ID inexistente")
    void findById_NotFound_ThrowsException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> productService.findById(1L));

        assertEquals("Product not found", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar produto que não existe")
    void delete_NotFound_ThrowsException() {
        when(productRepository.existsById(1L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> productService.delete(1L));
        verify(productRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Deve atualizar produto com novos dados")
    void update_Success() {
        Long productId = 100L;
        Product existingProduct = new Product();
        existingProduct.setId(productId);

        Company newCompany = new Company();
        newCompany.setUserId(20L);

        Category newCategory = new Category();
        newCategory.setId(2L);

        ProductRequest request = new ProductRequest();
        request.setCompanyId(20L);
        request.setCategoryId(2L);

        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(companyRepository.findById(20L)).thenReturn(Optional.of(newCompany));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(newCategory));
        when(productRepository.save(any())).thenReturn(existingProduct);

        productService.update(productId, request);

        verify(productMapper).updateProductFromRequest(eq(request), eq(existingProduct));
        assertEquals(newCompany, existingProduct.getCompany());
        assertEquals(newCategory, existingProduct.getCategory());
    }

    @Test
    @DisplayName("MUTANTE: Deve garantir que o preço 0.00 seja bloqueado (Boundary Test)")
    void mutation_PriceExactlyZero() {
        ProductRequest request = new ProductRequest();
        request.setPrice(BigDecimal.ZERO);
        request.setCompanyId(1L);
        request.setCategoryId(1L);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(new Company()));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));

        assertThrows(RuntimeException.class, () -> productService.create(request));
    }

    @Test
    @DisplayName("MUTANTE: Deve garantir que estoque zero seja bloqueado (Boundary Test)")
    void mutation_StockExactlyZero() {
        ProductRequest request = new ProductRequest();
        request.setPrice(BigDecimal.TEN);
        request.setStock(0);
        request.setCompanyId(1L);
        request.setCategoryId(1L);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(new Company()));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category()));

        assertThrows(RuntimeException.class, () -> productService.create(request));
    }

    @Test
    @DisplayName("MUTANTE: Deve garantir que o ID da categoria seja validado antes da busca")
    void mutation_FindById_ShouldThrowIfNotFound() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.findById(999L), "Product not found");
    }
}