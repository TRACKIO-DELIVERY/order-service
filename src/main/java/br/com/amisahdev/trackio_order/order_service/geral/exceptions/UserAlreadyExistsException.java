package br.com.amisahdev.trackio_order.order_service.geral.exceptions;

public class UserAlreadyExistsException extends RestrictionViolationException {
    public UserAlreadyExistsException() {
        super("User already exists");
    }
}
