package com.example.shop.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Интеграционный тест для /api/auth/register.
 * Проверяем, что новый пользователь успешно регистрируется
 * и в ответе есть валидный (не пустой) JWT-токен.
 */
public class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Успешная регистрация нового пользователя возвращает JWT-токен")
    void registerShouldReturnJwtToken() throws Exception {
        // уникальный email, чтобы не ловить конфликт по уникальному индексу
        String email = "test+" + UUID.randomUUID() + "@example.com";

        String requestJson = """
                {
                  "email": "%s",
                  "password": "StrongPass123!",
                  "fullName": "Test User"
                }
                """.formatted(email);

        var mvcResult = mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isOk())
                .andReturn();

        String body = mvcResult.getResponse().getContentAsString();
        JsonNode json = objectMapper.readTree(body);

        // ожидаем поле "token" в ответе
        assertTrue(json.hasNonNull("token"), "Ответ должен содержать поле 'token'");
        String token = json.get("token").asText();
        assertFalse(token.isBlank(), "JWT токен не должен быть пустым");

        // накидаем ещё минимальную проверку формата: две точки в JWT
        assertTrue(token.chars().filter(ch -> ch == '.').count() == 2,
                "JWT должен содержать три части, разделённые точками");
    }
}
