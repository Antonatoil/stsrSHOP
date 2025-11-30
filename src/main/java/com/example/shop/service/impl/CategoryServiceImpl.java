package com.example.shop.service.impl;

import com.example.shop.dto.category.CategoryDto;
import com.example.shop.dto.category.CreateCategoryRequestDto;
import com.example.shop.entity.Category;
import com.example.shop.exception.NotFoundException;
import com.example.shop.mapper.CategoryMapper;
import com.example.shop.repository.CategoryRepository;
import com.example.shop.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
        log.debug("Поиск категории по id={}", id);
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found"));
    }

    @Override
    public Category createIfNotExists(String name, String description) {
        log.debug("createIfNotExists: name={}, description={}", name, description);

        if (categoryRepository.existsByName(name)) {
            log.debug("Категория с name={} уже существует, ищем существующую", name);
            // старое поведение — ищем категорию по имени
            return categoryRepository.findAll().stream()
                    .filter(c -> c.getName().equals(name))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("Category not found"));
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        Category saved = categoryRepository.save(category);
        log.info("Создана новая категория (legacy): id={}, name={}", saved.getId(), saved.getName());
        return saved;
    }

    // ===== новые методы для REST API =====

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryDto> getCategories(Pageable pageable) {
        log.debug("Получение страницы категорий: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long id) {
        log.debug("Получение DTO категории по id={}", id);
        Category category = getById(id);
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto create(CreateCategoryRequestDto dto) {
        log.info("Создание категории через REST API: name={}", dto.getName());
        Category category = categoryMapper.toEntity(dto);
        Category saved = categoryRepository.save(category);
        log.info("Категория создана: id={}, name={}", saved.getId(), saved.getName());
        return categoryMapper.toDto(saved);
    }

    @Override
    public CategoryDto update(Long id, CreateCategoryRequestDto dto) {
        log.info("Обновление категории id={} новым name={}", id, dto.getName());
        Category category = getById(id);
        categoryMapper.updateCategoryFromDto(dto, category);
        Category saved = categoryRepository.save(category);
        log.info("Категория обновлена: id={}, name={}", saved.getId(), saved.getName());
        return categoryMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        log.warn("Запрос на удаление категории id={}", id);
        if (!categoryRepository.existsById(id)) {
            log.warn("Категория для удаления не найдена: id={}", id);
            throw new NotFoundException("Category not found");
        }
        categoryRepository.deleteById(id);
        log.info("Категория удалена: id={}", id);
    }
}
