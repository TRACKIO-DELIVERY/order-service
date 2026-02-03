package br.com.amisahdev.trackio_order.order_service.geral.exceptions;

public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
