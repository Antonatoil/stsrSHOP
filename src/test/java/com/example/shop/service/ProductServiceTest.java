package com.example.shop.service;

import com.example.shop.entity.Category;
import com.example.shop.entity.Product;
import com.example.shop.integration.BaseIntegrationTest;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ProductServiceTest extends BaseIntegrationTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    @Transactional
    @DisplayName("Создание категории и продукта, сохранение и загрузка из БД")
    void createProductAndLoadItFromDatabase() {
        Category category = new Category();
        category.setName("Test Category");
        category.setDescription("Created in integration test");

        Category savedCategory = categoryRepository.save(category);
        assertNotNull(savedCategory.getId(), "ID категории должен быть заполнен");

        Product product = new Product();
        product.setName("Integration Test Product");
        product.setDescription("Product created in integration test");
        product.setPrice(BigDecimal.valueOf(999.99));
        product.setStock(5);
        product.setActive(true);
        product.setCategory(savedCategory);

        Product savedProduct = productRepository.save(product);

        assertNotNull(savedProduct.getId(), "ID продукта должен быть заполнен");
        assertEquals("Integration Test Product", savedProduct.getName());
        assertEquals(savedCategory.getId(), savedProduct.getCategory().getId());

        List<Product> allProducts = productRepository.findAll();
        boolean found = allProducts.stream()
                .anyMatch(p -> "Integration Test Product".equals(p.getName()));

        assertTrue(found, "Сохранённый продукт должен находиться в базе");
    }
}
