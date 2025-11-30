package com.example.shop.exception;

/**
 * Простейшее исключение "ресурс не найден",
 * которое можно кидать из сервисов/репозиториев.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
