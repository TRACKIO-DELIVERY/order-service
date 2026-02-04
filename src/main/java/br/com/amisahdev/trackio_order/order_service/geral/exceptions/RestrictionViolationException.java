package br.com.amisahdev.trackio_order.order_service.geral.exceptions;

public class RestrictionViolationException extends RuntimeException {
    public RestrictionViolationException(String message) {
        super(message);
    }
}
