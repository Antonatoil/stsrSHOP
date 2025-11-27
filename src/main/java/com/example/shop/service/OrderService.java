package com.example.shop.service;

import com.example.shop.dto.order.CreateOrderRequestDto;
import com.example.shop.dto.order.OrderDto;
import com.example.shop.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    OrderDto create(CreateOrderRequestDto dto);

    OrderDto getById(Long id);

    Page<OrderDto> getMyOrders(Pageable pageable);

    Page<OrderDto> getAllOrders(Pageable pageable);

    // новый метод
    OrderDto updateStatus(Long id, OrderStatus status);
}
