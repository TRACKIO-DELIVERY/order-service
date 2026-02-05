package br.com.amisahdev.trackio_order.order_service.geral.exceptions;

public class CategoryAlreadyExistsException extends RestrictionViolationException {
    public CategoryAlreadyExistsException() {
        super("Category already exists");
    }
}
