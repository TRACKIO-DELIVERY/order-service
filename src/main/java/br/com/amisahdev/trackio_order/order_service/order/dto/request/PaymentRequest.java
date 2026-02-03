package br.com.amisahdev.trackio_order.order_service.order.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class PaymentRequest {
    @NotBlank(message = "PaymentMethod not blank")
    private String paymentMethod;
}
