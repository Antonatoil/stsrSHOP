package com.example.shop.controller;

import com.example.shop.dto.category.CategoryDto;
import com.example.shop.dto.category.CreateCategoryRequestDto;
import com.example.shop.service.CategoryService;
import com.example.shop.util.PaginationUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // GET /api/categories?page=&size=
    @GetMapping
    public Page<CategoryDto> getCategories(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.defaultPageable(page, size);
        log.debug("Запрос списка категорий: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return categoryService.getCategories(pageable);
    }

    // GET /api/categories/{id}
    @GetMapping("/{id}")
    public CategoryDto getById(@PathVariable Long id) {
        log.debug("Запрос категории по id={}", id);
        // ВАЖНО: используем DTO-метод, а не entity-метод
        return categoryService.getCategoryById(id);
    }

    // POST /api/categories
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto create(@Valid @RequestBody CreateCategoryRequestDto dto) {
        log.info("Создание категории: name={}", dto.getName());
        return categoryService.create(dto);
    }

    // PUT /api/categories/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto update(
            @PathVariable Long id,
            @Valid @RequestBody CreateCategoryRequestDto dto
    ) {
        log.info("Обновление категории id={} новым name={}", id, dto.getName());
        return categoryService.update(id, dto);
    }

    // DELETE /api/categories/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        log.warn("Удаление категории id={}", id);
        categoryService.delete(id);
    }
}
