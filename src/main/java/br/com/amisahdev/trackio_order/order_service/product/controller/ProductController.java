package br.com.amisahdev.trackio_order.order_service.product.controller;


import br.com.amisahdev.trackio_order.order_service.product.dto.request.ProductRequest;
import br.com.amisahdev.trackio_order.order_service.product.dto.response.ProductResponse;
import br.com.amisahdev.trackio_order.order_service.product.service.interf.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestPart("product") ProductRequest request,
            @RequestPart("image") MultipartFile image) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(request, image));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestPart("request") ProductRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(productService.update(id, request, image));
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
