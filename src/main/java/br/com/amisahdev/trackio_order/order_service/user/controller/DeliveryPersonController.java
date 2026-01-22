package br.com.amisahdev.trackio_order.order_service.user.controller;


import br.com.amisahdev.trackio_order.order_service.user.dto.request.DeliveryPersonRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.DeliveryPersonResponse;
import br.com.amisahdev.trackio_order.order_service.user.models.DeliveryPerson;
import br.com.amisahdev.trackio_order.order_service.user.service.interf.DeliveryPersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveryperson")
@RequiredArgsConstructor
public class DeliveryPersonController {
    private final DeliveryPersonService deliveryPersonService;


    @PostMapping
    public ResponseEntity<DeliveryPersonResponse> create(@Valid @RequestBody DeliveryPersonRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(deliveryPersonService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryPersonResponse> findById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(deliveryPersonService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryPersonResponse> update(@PathVariable Long id, @Valid @RequestBody DeliveryPersonRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(deliveryPersonService.update(id,request));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        deliveryPersonService.delete(id);
    }
}
