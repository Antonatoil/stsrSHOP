package com.example.shop.controller;

import com.example.shop.dto.product.CreateProductRequestDto;
import com.example.shop.dto.product.ProductDto;
import com.example.shop.service.ProductService;
import com.example.shop.util.PaginationUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Page<ProductDto> getProducts(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.defaultPageable(page, size);
        log.debug("Запрос списка товаров: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());
        return productService.getProducts(pageable);
    }

    @GetMapping("/{id}")
    public ProductDto getById(@PathVariable Long id) {
        log.debug("Запрос товара по id={}", id);
        return productService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDto create(@Valid @RequestBody CreateProductRequestDto dto) {
        log.info("Создание товара: name={}", dto.getName());
        return productService.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductDto update(
            @PathVariable Long id,
            @Valid @RequestBody CreateProductRequestDto dto
    ) {
        log.info("Обновление товара id={} новым name={}", id, dto.getName());
        return productService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        log.warn("Удаление товара id={}", id);
        productService.delete(id);
    }
}
