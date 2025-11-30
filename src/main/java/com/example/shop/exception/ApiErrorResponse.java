package com.example.shop.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Единый формат ответа об ошибке для REST API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    /**
     * HTTP статус (например, 400, 403, 404, 500).
     */
    private int status;

    /**
     * Краткое текстовое описание статуса (Bad Request, Forbidden, Not Found, ...).
     */
    private String error;

    /**
     * Человеко-читаемое сообщение об ошибке.
     */
    private String message;

    /**
     * URI запроса, на котором произошла ошибка.
     */
    private String path;

    /**
     * Время, когда ошибка произошла.
     */
    private LocalDateTime timestamp;

    /**
     * Детали валидационных ошибок (если есть).
     */
    private List<FieldValidationError> validationErrors;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldValidationError {
        private String field;
        private String message;
    }
}
