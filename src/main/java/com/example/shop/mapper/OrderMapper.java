package com.example.shop.mapper;

import com.example.shop.dto.order.OrderDto;
import com.example.shop.dto.order.OrderItemDto;
import com.example.shop.entity.Order;
import com.example.shop.entity.OrderItem;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "status", expression = "java(order.getStatus().name())")
    @Mapping(target = "items", ignore = true)
    OrderDto toDto(Order order);

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemDto toItemDto(OrderItem item);

    @AfterMapping
    default void mapItems(Order order, @MappingTarget OrderDto dto) {
        if (order.getItems() != null) {
            List<OrderItemDto> itemDtos = order.getItems().stream()
                    .map(this::toItemDto)
                    .collect(Collectors.toList());
            dto.setItems(itemDtos);
        }
    }
}
