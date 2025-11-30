package com.example.shop.service.impl;

import com.example.shop.dto.product.CreateProductRequestDto;
import com.example.shop.dto.product.ProductDto;
import com.example.shop.entity.Category;
import com.example.shop.entity.Product;
import com.example.shop.exception.NotFoundException;
import com.example.shop.mapper.ProductMapper;
import com.example.shop.repository.ProductRepository;
import com.example.shop.service.CategoryService;
import com.example.shop.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryService categoryService,
                              ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
    }

    /**
     * Список товаров — только активные, без кэша.
     */
    @Override
    public Page<ProductDto> getProducts(Pageable pageable) {
        log.debug("Получение списка активных товаров: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return productRepository.findAllByActiveTrue(pageable)
                .map(productMapper::toDto);
    }

    /**
     * Один товар по id — кэшируем (даже если он неактивен, тк он мог быть в заказах).
     */
    @Override
    @Cacheable(cacheNames = "products", key = "#id")
    public ProductDto getById(Long id) {
        log.debug("Получение товара по id={} (кэшируемый метод)", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));
        return productMapper.toDto(product);
    }

    /**
     * Создание товара — очищаем кэш списка.
     */
    @Override
    @CacheEvict(cacheNames = "products", allEntries = true)
    public ProductDto create(CreateProductRequestDto dto) {
        log.info("Создание товара: name={}, categoryId={}", dto.getName(), dto.getCategoryId());
        Category category = categoryService.getById(dto.getCategoryId());

        Product product = productMapper.toEntity(dto);
        product.setCategory(category);
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());

        Product saved = productRepository.save(product);
        log.info("Товар создан: id={}, name={}", saved.getId(), saved.getName());
        return productMapper.toDto(saved);
    }

    /**
     * Обновление товара — очищаем кэш.
     */
    @Override
    @CacheEvict(cacheNames = "products", allEntries = true)
    public ProductDto update(Long id, CreateProductRequestDto dto) {
        log.info("Обновление товара id={} новым name={}, categoryId={}",
                id, dto.getName(), dto.getCategoryId());

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        Category category = categoryService.getById(dto.getCategoryId());

        productMapper.updateProductFromDto(dto, product);
        product.setCategory(category);

        Product saved = productRepository.save(product);
        log.info("Товар обновлён: id={}, name={}", saved.getId(), saved.getName());
        return productMapper.toDto(saved);
    }

    /**
     * "Удаление" товара — делаем soft delete: active = false.
     * Заказы не ломаются, FK не ругается.
     */
    @Override
    @CacheEvict(cacheNames = "products", allEntries = true)
    public void delete(Long id) {
        log.warn("Запрос на удаление (soft delete) товара id={}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        product.setActive(false);
        productRepository.save(product);
        log.info("Товар помечен как неактивный (soft delete): id={}", id);
    }
}
