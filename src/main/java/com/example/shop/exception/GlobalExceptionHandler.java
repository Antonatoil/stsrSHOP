package com.example.shop.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 404 — неизвестный endpoint.
     *
     * Ожидаемый JSON (по сути):
     * {
     *   "status": 404,
     *   "error": "Not Found",
     *   "message": "No endpoint GET /api/definitely-not-existing-url.",
     *   "timestamp": "..."
     * }
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(NoHandlerFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        String message = "No endpoint " + ex.getHttpMethod() + " " + ex.getRequestURL() + ".";
        log.warn("NoHandlerFoundException: {}", message);

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())     // "Not Found"
                .message(message)
                .timestamp(OffsetDateTime.now())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    /**
     * 400 — ошибки валидации (@Valid).
     *
     * Тест явно ждёт поле $.error, плюс errors по полям.
     *
     * Пример JSON:
     * {
     *   "status": 400,
     *   "error": "Bad Request",
     *   "message": "Validation failed",
     *   "errors": {
     *     "email": "...",
     *     "password": "...",
     *     "fullName": "..."
     *   },
     *   "timestamp": "..."
     * }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = (error instanceof FieldError fe)
                    ? fe.getField()
                    : error.getObjectName();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())     // "Bad Request"
                .message("Validation failed")
                .errors(fieldErrors)                 // важно: сохраняем старую структуру errors
                .timestamp(OffsetDateTime.now())
                .build();

        return ResponseEntity.status(status).body(body);
    }

    /**
     * 500 — всё остальное.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAny(Exception ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        log.error("Unexpected error", ex);

        ApiErrorResponse body = ApiErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())     // "Internal Server Error"
                .message(ex.getMessage())
                .timestamp(OffsetDateTime.now())
                .build();

        return ResponseEntity.status(status).body(body);
    }
}
