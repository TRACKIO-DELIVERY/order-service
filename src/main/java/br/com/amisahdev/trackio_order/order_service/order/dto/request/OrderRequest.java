package br.com.amisahdev.trackio_order.order_service.order.dto.request;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderRequest {

    @NotNull(message = "Company is required")
    private Long companyId;

    @NotNull(message = "Customer is required")
    private Long customerId;

//    @NotNull(message = "OrderDate is required")
//    private LocalDateTime orderDate;
//
//    @NotNull(message = "orderAmount is required")
//    @Positive(message = "orderAmount must be greater than zero")
//    private BigDecimal orderAmount;
    List<OrderItemRequest> items;
    PaymentRequest payment;
}
