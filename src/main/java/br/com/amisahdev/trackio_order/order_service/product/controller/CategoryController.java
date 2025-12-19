package br.com.amisahdev.trackio_order.order_service.product.controller;

import br.com.amisahdev.trackio_order.order_service.product.dto.request.CategoryRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.CategoryResponse;
import br.com.amisahdev.trackio_order.order_service.product.repository.CategoryRepository;
import br.com.amisahdev.trackio_order.order_service.product.service.imp.CategoryServiceImp;
import br.com.amisahdev.trackio_order.order_service.product.service.interf.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.findById(id));
    }

    @GetMapping("/company/{id}")
    public ResponseEntity<List<CategoryResponse>> findAllByCompanyId(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.findAllByCompany_Id(id));
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        categoryService.delete(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.update(id, request));
    }
}
