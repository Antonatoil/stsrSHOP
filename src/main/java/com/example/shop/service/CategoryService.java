package com.example.shop.service;

import com.example.shop.dto.category.CategoryDto;
import com.example.shop.dto.category.CreateCategoryRequestDto;
import com.example.shop.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    Category getById(Long id);

    Category createIfNotExists(String name, String description);


    Page<CategoryDto> getCategories(Pageable pageable);

    CategoryDto getCategoryById(Long id);

    CategoryDto create(CreateCategoryRequestDto dto);

    CategoryDto update(Long id, CreateCategoryRequestDto dto);

    void delete(Long id);
}
