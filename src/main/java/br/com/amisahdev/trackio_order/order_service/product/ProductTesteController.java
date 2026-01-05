package br.com.amisahdev.trackio_order.order_service.product;


import br.com.amisahdev.trackio_order.order_service.product.model.Product;
import br.com.amisahdev.trackio_order.order_service.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/teste/product")
public class ProductTesteController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(@Valid @RequestBody Product product) {
        productRepository.save(product);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "The product was created");
        response.put("status", 201);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> response = new HashMap<>();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            response.put("error", "`" + error.getField() + "` " + error.getDefaultMessage());
            response.put("status", 400);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        Map<String, Object> response = new HashMap<>();

        String message = ex.getMessage();
        if (message.contains("BigDecimal")) {
            response.put("error", "`price` should be a double");
        } else if (message.contains("Integer")) {
            response.put("error", "`stock` should be a int");
        } else {
            response.put("error", "Invalid request format");
        }

        response.put("status", 400);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
