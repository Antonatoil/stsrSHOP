package com.example.shop.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCategoryRequestDto {

    @NotBlank
    @Size(min = 2, max = 255)
    private String name;

    @Size(max = 1024)
    private String description;

    public CreateCategoryRequestDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
