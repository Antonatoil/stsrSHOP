package com.example.shop.mapper;

import com.example.shop.dto.category.CategoryDto;
import com.example.shop.dto.category.CreateCategoryRequestDto;
import com.example.shop.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDto toDto(Category category);

    Category toEntity(CreateCategoryRequestDto dto);

    void updateCategoryFromDto(CreateCategoryRequestDto dto,
                               @MappingTarget Category category);
}
