package com.example.shop.service;

import com.example.shop.entity.Category;
import com.example.shop.entity.Product;
import com.example.shop.integration.BaseIntegrationTest;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Интеграционный тест работы с продуктами на уровне JPA.
 * Использует реальную БД (Postgres в Testcontainers).
 */
public class ProductServiceTest extends BaseIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @Transactional
    void createProductAndLoadItFromDatabase() {
        // 1. создаём категорию
        Category category = new Category();
        category.setName("Test Category");
        category.setDescription("Created in integration test");

        Category savedCategory = categoryRepository.save(category);
        assertNotNull(savedCategory.getId(), "ID категории должен быть заполнен");

        // 2. создаём продукт
        Product product = new Product();
        product.setName("Integration Test Product");
        product.setDescription("Product created in integration test");
        product.setPrice(BigDecimal.valueOf(999.99));
        product.setStock(5);
        product.setCategory(savedCategory);

        Product savedProduct = productRepository.save(product);

        assertNotNull(savedProduct.getId(), "ID продукта должен быть заполнен");
        assertEquals("Integration Test Product", savedProduct.getName());
        assertEquals(savedCategory.getId(), savedProduct.getCategory().getId());

        // 3. достаём все продукты и убеждаемся, что наш там есть
        List<Product> allProducts = productRepository.findAll();
        boolean found = allProducts.stream()
                .anyMatch(p -> "Integration Test Product".equals(p.getName()));

        assertTrue(found, "Сохранённый продукт должен находиться в базе");
    }
}
