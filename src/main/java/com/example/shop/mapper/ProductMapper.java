package com.example.shop.mapper;

import com.example.shop.dto.product.CreateProductRequestDto;
import com.example.shop.dto.product.ProductDto;
import com.example.shop.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Entity -> DTO
     */
    @Mapping(target = "categoryId", source = "category.id")
    ProductDto toDto(Product product);

    /**
     * DTO -> Entity при создании.
     * id, createdAt, active, category игнорируем — их ставим в сервисе.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toEntity(CreateProductRequestDto dto);

    /**
     * Обновление товара из DTO.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "category", ignore = true)
    void updateProductFromDto(CreateProductRequestDto dto,
                              @MappingTarget Product product);
}
