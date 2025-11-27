package com.example.shop.service;

import com.example.shop.dto.product.CreateProductRequestDto;
import com.example.shop.dto.product.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    Page<ProductDto> getProducts(Pageable pageable);

    ProductDto getById(Long id);

    ProductDto create(CreateProductRequestDto dto);

    ProductDto update(Long id, CreateProductRequestDto dto);

    void delete(Long id);
}
