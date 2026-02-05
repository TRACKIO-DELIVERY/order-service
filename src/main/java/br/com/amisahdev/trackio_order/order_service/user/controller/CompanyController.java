package br.com.amisahdev.trackio_order.order_service.user.controller;


import br.com.amisahdev.trackio_order.order_service.user.dto.request.CompanyRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.CompanyResponse;
import br.com.amisahdev.trackio_order.order_service.user.dto.validation.NotRequiresExpoToken;
import br.com.amisahdev.trackio_order.order_service.user.service.interf.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.attribute.standard.Media;


@RestController
@RequestMapping("/api/company")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponse> create(
            @Validated(NotRequiresExpoToken.class)
            @Valid @RequestBody CompanyRequest request) {

        CompanyResponse companyResponse = companyService.create(request,null);
        return ResponseEntity.status(HttpStatus.CREATED).body(companyResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(companyService.findById(id));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<CompanyResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody CompanyRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(companyService.update(id, request,null));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        companyService.delete(id);
    }
}
