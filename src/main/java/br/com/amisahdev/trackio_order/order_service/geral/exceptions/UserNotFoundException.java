package br.com.amisahdev.trackio_order.order_service.geral.exceptions;

public class UserNotFoundException extends BusinessException {
    public UserNotFoundException() {
        super("User not found");
    }
}
