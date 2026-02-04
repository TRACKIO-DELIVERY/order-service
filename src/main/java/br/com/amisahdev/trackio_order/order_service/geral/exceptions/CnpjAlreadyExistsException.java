package br.com.amisahdev.trackio_order.order_service.geral.exceptions;

public class CnpjAlreadyExistsException extends RestrictionViolationException {
    public CnpjAlreadyExistsException() {
        super("CNPJ already exists");
    }
}
