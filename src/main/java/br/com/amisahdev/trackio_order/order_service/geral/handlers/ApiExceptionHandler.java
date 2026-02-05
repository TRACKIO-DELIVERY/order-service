package br.com.amisahdev.trackio_order.order_service.geral.handlers;

import br.com.amisahdev.trackio_order.order_service.geral.exceptions.RestrictionViolationException;
import br.com.amisahdev.trackio_order.order_service.geral.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayDeque;

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

    @ExceptionHandler(RestrictionViolationException.class)
    public ResponseEntity<Object> handleRestrictionViolationException(final RestrictionViolationException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(final RuntimeException ex) {
        return buildErrorResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        var errorResponseBuilder = ErrorResponse.builder();
        errorResponseBuilder.code(HttpStatus.BAD_REQUEST.value());
        errorResponseBuilder.message("Validation error");
        errorResponseBuilder.fields(new ArrayDeque<>());

        final var errorResponse = errorResponseBuilder.build();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            ErrorField fieldError = ErrorField.builder()
                    .field(error.getField())
                    .message(error.getDefaultMessage())
                    .build();

            errorResponse.getFields().add(fieldError);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
