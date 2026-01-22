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

    @PatchMapping("/canceled")
    public ResponseEntity<OrderResponse> cancelOrder(@Valid @RequestBody OrderCompletedCanceledRequest request) {
        return ResponseEntity.status((HttpStatus.OK)).body(orderService.orderCancel(request));
    }

    @PatchMapping("/completed")
    public ResponseEntity<OrderResponse> completedOrder(@Valid @RequestBody OrderCompletedCanceledRequest request) {
        return ResponseEntity.status((HttpStatus.OK)).body(orderService.orderCompleted(request));
    }

}
