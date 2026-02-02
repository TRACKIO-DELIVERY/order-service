package br.com.amisahdev.trackio_order.order_service.geral.handlers;

import br.com.amisahdev.trackio_order.order_service.geral.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    protected ResponseEntity<Object> buildErrorResponse(final String message, final HttpStatus status) {
        final var errorResponse = ErrorResponse.builder()
                .message(message)
                .code(status.value())
                .build();
        return ResponseEntity.status(status).body(errorResponse);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(final UserNotFoundException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
