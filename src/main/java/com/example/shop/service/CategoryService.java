package com.example.shop.service;

import com.example.shop.dto.category.CategoryDto;
import com.example.shop.dto.category.CreateCategoryRequestDto;
import com.example.shop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    // Старый метод — нужен для ProductService и других внутренних сервисов
    Category getById(Long id);

    // Старый метод — оставить для совместимости
    Category createIfNotExists(String name, String description);

    // ===== Новый функционал для REST API =====

    Page<CategoryDto> getCategories(Pageable pageable);

    CategoryDto getCategoryById(Long id);

    CategoryDto create(CreateCategoryRequestDto dto);

    CategoryDto update(Long id, CreateCategoryRequestDto dto);

    void delete(Long id);
}
