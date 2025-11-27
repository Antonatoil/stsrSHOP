package com.example.shop.controller;

import com.example.shop.dto.order.CreateOrderRequestDto;
import com.example.shop.dto.order.OrderDto;
import com.example.shop.service.OrderService;
import com.example.shop.util.PaginationUtil;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Создать заказ (корзина -> заказ).
     * Доступно USER и ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER') or hasAuthority('ROLE_ADMIN')")
    public OrderDto create(@Valid @RequestBody CreateOrderRequestDto dto) {
        return orderService.create(dto);
    }

    /**
     * Получить заказ по id.
     * Любой залогиненный (USER или ADMIN).
     */
    @GetMapping("/{id}")
    public OrderDto getById(@PathVariable Long id) {
        return orderService.getById(id);
    }

    /**
     * Мои заказы (по текущему юзеру из SecurityContext).
     * USER и ADMIN — оба могут видеть свои заказы.
     * @PreAuthorize не ставим, достаточно .anyRequest().authenticated() в SecurityConfig.
     */
    @GetMapping("/my")
    public Page<OrderDto> getMyOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.defaultPageable(page, size);
        return orderService.getMyOrders(pageable);
    }

    /**
     * Все заказы в системе — только для ADMIN.
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public Page<OrderDto> getAllOrders(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        Pageable pageable = PaginationUtil.defaultPageable(page, size);
        return orderService.getAllOrders(pageable);
    }

    /**
     * Обновление статуса заказа — только ADMIN.
     */
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public OrderDto updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid com.example.shop.dto.order.UpdateOrderStatusRequestDto dto
    ) {
        return orderService.updateStatus(id, dto.getStatus());
    }
}

