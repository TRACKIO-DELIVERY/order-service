package br.com.amisahdev.trackio_order.order_service.geral.exceptions;

public class CategoryNotFoundException extends BusinessException {
    public CategoryNotFoundException() {
        super("Category not found");
    }
}