package com.example.shop.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик ошибок для REST-контроллеров.
 * Превращает исключения в единый JSON-формат ApiErrorResponse.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---------- 400 BAD REQUEST ----------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<ApiErrorResponse.FieldValidationError> validationErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> new ApiErrorResponse.FieldValidationError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ApiErrorResponse body = baseError(
                HttpStatus.BAD_REQUEST,
                "Ошибка валидации входных данных",
                request.getRequestURI()
        );
        body.setValidationErrors(validationErrors);

        log.warn("Validation error on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        List<ApiErrorResponse.FieldValidationError> validationErrors = ex.getConstraintViolations()
                .stream()
                .map(violation -> new ApiErrorResponse.FieldValidationError(
                        violation.getPropertyPath().toString(),
                        violation.getMessage()
                ))
                .collect(Collectors.toList());

        ApiErrorResponse body = baseError(
                HttpStatus.BAD_REQUEST,
                "Ошибка валидации параметров запроса",
                request.getRequestURI()
        );
        body.setValidationErrors(validationErrors);

        log.warn("Constraint violation on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MissingServletRequestParameterException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            Exception ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = baseError(
                HttpStatus.BAD_REQUEST,
                "Неверный формат запроса",
                request.getRequestURI()
        );

        log.warn("Bad request on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // ---------- 403 FORBIDDEN ----------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiErrorResponse> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = baseError(
                HttpStatus.FORBIDDEN,
                "Доступ запрещён",
                request.getRequestURI()
        );

        log.warn("Access denied on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    // ---------- 404 NOT FOUND ----------

    /**
     * 404 для наших доменных сущностей (Product, Category и т.д.).
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = baseError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI()
        );

        log.info("Resource not found on {}: {}", request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * 404, если вообще нет хендлера под такой URL.
     * Работает вместе с настройками spring.mvc.throw-exception-if-no-handler-found=true.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoHandlerFound(
            NoHandlerFoundException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = baseError(
                HttpStatus.NOT_FOUND,
                "Ресурс не найден",
                request.getRequestURI()
        );

        log.info("No handler found for {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // ---------- 405 METHOD NOT ALLOWED (опционально, но красиво) ----------

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = baseError(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Метод не поддерживается для данного URL",
                request.getRequestURI()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setAllow(ex.getSupportedHttpMethods());

        log.warn("Method not allowed on {}: {}", request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(body, headers, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // ---------- 500 INTERNAL SERVER ERROR ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception ex,
            HttpServletRequest request
    ) {
        ApiErrorResponse body = baseError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Внутренняя ошибка сервера",
                request.getRequestURI()
        );

        log.error("Unexpected error on {}:", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    // ---------- ВСПОМОГАТЕЛЬНЫЙ МЕТОД ----------

    private ApiErrorResponse baseError(HttpStatus status, String message, String path) {
        return ApiErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
