package com.example.shop.service.impl;

import com.example.shop.dto.category.CategoryDto;
import com.example.shop.dto.category.CreateCategoryRequestDto;
import com.example.shop.entity.Category;
import com.example.shop.exception.NotFoundException;
import com.example.shop.mapper.CategoryMapper;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    // ===== старые методы (для совместимости) =====

    @Override
    @Transactional(readOnly = true)
    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Override
    public Category createIfNotExists(String name, String description) {
        if (categoryRepository.existsByName(name)) {
            // старое поведение — ищем категорию по имени
            return categoryRepository.findAll().stream()
                    .filter(c -> c.getName().equals(name))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Category not found"));
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        return categoryRepository.save(category);
    }

    // ===== новые методы для REST API =====

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDto> getCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        Category category = getById(id);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto create(CreateCategoryRequestDto dto) {
        Category category = categoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto dto) {
        Category category = getById(id);
        categoryMapper.updateCategoryFromDto(dto, category);
        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
    }
}
