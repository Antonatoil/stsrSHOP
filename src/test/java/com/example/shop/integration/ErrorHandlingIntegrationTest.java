package com.example.shop.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционные тесты для глобальной обработки ошибок.
 */
public class ErrorHandlingIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Проверяем, что несуществующий URL отдаёт 404 в нашем формате.
     */
    @Test
    void unknownEndpointShouldReturnUnified404() throws Exception {
        mockMvc.perform(get("/api/definitely-not-existing-url"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message").value("Ресурс не найден"))
                .andExpect(jsonPath("$.path", is("/api/definitely-not-existing-url")))
                .andExpect(jsonPath("$.timestamp", notNullValue()));
    }

    /**
     * Проверяем 400: кривой/некорректный запрос на регистрацию пользователя.
     * Здесь предполагаем, что на DTO стоят @Valid-аннотации.
     */
    @Test
    void invalidRegisterRequestShouldReturnUnified400() throws Exception {
        String badJson = """
                {
                  "email": "not-an-email",
                  "password": "",
                  "fullName": ""
                }
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(badJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message").value("Ошибка валидации входных данных"))
                .andExpect(jsonPath("$.path", is("/api/auth/register")))
                .andExpect(jsonPath("$.timestamp", notNullValue()))
                .andExpect(jsonPath("$.validationErrors").isArray());
    }
}
