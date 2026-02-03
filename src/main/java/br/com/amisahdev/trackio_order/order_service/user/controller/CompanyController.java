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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompanyResponse> create(
            @Validated(NotRequiresExpoToken.class)
            @Valid @RequestPart CompanyRequest request,
            @RequestPart(value = "image", required = false)MultipartFile image) {

        CompanyResponse companyResponse = companyService.create(request,image);
        return ResponseEntity.status(HttpStatus.CREATED).body(companyResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(companyService.findById(id));
    }

    @PutMapping(value = "/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CompanyResponse> update(
            @PathVariable Long id,
            @Valid @RequestPart CompanyRequest request,
            @RequestPart("image") MultipartFile image) {
        return ResponseEntity.status(HttpStatus.OK).body(companyService.update(id, request,image));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id){
        companyService.delete(id);
    }
}
