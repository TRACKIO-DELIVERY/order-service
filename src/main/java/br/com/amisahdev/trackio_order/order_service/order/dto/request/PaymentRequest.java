package br.com.amisahdev.trackio_order.order_service.order.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentRequest {

    @NotNull(message = "orderId is required")
    private Long order;

    @NotBlank(message = "PaymentMethod not blank")
    private String paymentMethod;

    @Positive(message = "amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "PaymentDate is required")
    private LocalDateTime paymentDate;
}
