package br.com.amisahdev.trackio_order.order_service.product.controller;


import br.com.amisahdev.trackio_order.order_service.product.dto.request.ProductRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.ProductResponse;
import br.com.amisahdev.trackio_order.order_service.product.service.interf.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(@PathVariable long id, @Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.update(id,request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponse>> findByCategoryId(@PathVariable Long categoryId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findByCategoryId(categoryId));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ProductResponse>> findByCompanyId(@PathVariable Long companyId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findByCompanyId(companyId));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<ProductResponse>> findByCategoryAndCompany(
            @RequestParam Long categoryId,
            @RequestParam Long companyId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findByCategoryAndCompanyId(categoryId, companyId));
    }


}
