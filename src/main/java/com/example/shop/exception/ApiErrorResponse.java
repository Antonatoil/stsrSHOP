package com.example.shop.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    /** HTTP статус-код (400, 404, 500, ...) */
    private int status;

    /** Краткое описание статуса ("Bad Request", "Not Found", "Internal Server Error") */
    private String error;

    /** Человеко-читаемое сообщение */
    private String message;

    /** Время возникновения ошибки */
    private OffsetDateTime timestamp;

    /** Для ошибок валидации: field -> message */
    private Map<String, String> errors;
}
