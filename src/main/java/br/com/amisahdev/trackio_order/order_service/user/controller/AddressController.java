package br.com.amisahdev.trackio_order.order_service.user.controller;


import br.com.amisahdev.trackio_order.order_service.user.dto.request.AddressRequest;
import br.com.amisahdev.trackio_order.order_service.user.dto.response.AddressResponse;
import br.com.amisahdev.trackio_order.order_service.user.service.interf.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponse> create(@Valid @RequestBody AddressRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AddressResponse> findById(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(addressService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AddressResponse> update(@PathVariable Long id, @Valid @RequestBody AddressRequest request){
        return ResponseEntity.status(HttpStatus.OK).body(addressService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public void  delete(@PathVariable Long id){
        addressService.delete(id);
    }
}
