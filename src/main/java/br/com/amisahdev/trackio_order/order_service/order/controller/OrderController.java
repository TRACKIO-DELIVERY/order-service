package br.com.amisahdev.trackio_order.order_service.order.controller;


import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderCompletedCanceledRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderDeliveryRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.request.OrderRequest;
import br.com.amisahdev.trackio_order.order_service.order.dto.response.OrderResponse;
import br.com.amisahdev.trackio_order.order_service.order.service.interf.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.create(orderRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> findbyid(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findById(id));
    }

    @PatchMapping("/in_route")
    public ResponseEntity<OrderResponse> updateStatus(@Valid @RequestBody OrderDeliveryRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.orderDelivery(request));
    }

    @PatchMapping("/cancelled")
    public ResponseEntity<OrderResponse> cancelOrder(@Valid @RequestBody OrderCompletedCanceledRequest request) {
        return ResponseEntity.status((HttpStatus.OK)).body(orderService.orderCancel(request));
    }

    @PatchMapping("/completed")
    public ResponseEntity<OrderResponse> completedOrder(@Valid @RequestBody OrderCompletedCanceledRequest request) {
        return ResponseEntity.status((HttpStatus.OK)).body(orderService.orderCompleted(request));
    }

    @GetMapping("/findall")
    public ResponseEntity<List<OrderResponse>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findAll());
    }

    @GetMapping("/findall/In_Route")
    public ResponseEntity<List<OrderResponse>> findAllInRoute() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findAllInRoute());
    }

    @GetMapping("/findall/In_Progress")
    public ResponseEntity<List<OrderResponse>> findAllInProgress() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findAllInProgress());
    }

    @GetMapping("/findall/Cancelled")
    public ResponseEntity<List<OrderResponse>> findAllCancelled() {
        return  ResponseEntity.status(HttpStatus.OK).body(orderService.findAllInCancelled());
    }

    @GetMapping("/findall/Completed")
    public ResponseEntity<List<OrderResponse>> findAllCompleted() {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findAllInCompleted());
    }

    @GetMapping("/company/{id}")
    public ResponseEntity<List<OrderResponse>> findByCompany(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(orderService.findByCompanyId(id));
    }

}
