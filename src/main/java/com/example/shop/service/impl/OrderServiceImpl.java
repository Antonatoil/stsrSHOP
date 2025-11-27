package com.example.shop.service.impl;

import com.example.shop.dto.order.CreateOrderRequestDto;
import com.example.shop.dto.order.OrderDto;
import com.example.shop.entity.Order;
import com.example.shop.entity.OrderItem;
import com.example.shop.entity.Product;
import com.example.shop.entity.User;
import com.example.shop.entity.enums.OrderStatus;
import com.example.shop.exception.NotFoundException;
import com.example.shop.mapper.OrderMapper;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import com.example.shop.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;

    public OrderServiceImpl(OrderRepository orderRepository,
                            UserRepository userRepository,
                            ProductRepository productRepository,
                            OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderMapper = orderMapper;
    }

    /**
     * Создать заказ для текущего залогиненного пользователя.
     */
    @Override
    public OrderDto create(CreateOrderRequestDto dto) {
        // 1. Достаём e-mail текущего пользователя из SecurityContext
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        // 2. Создаём Order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        // 3. Пробегаемся по позициям заказа
        for (var itemDto : dto.getItems()) {
            Long productId = itemDto.getProductId();
            Integer quantity = itemDto.getQuantity();

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found"));

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice());

            BigDecimal lineTotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(quantity));
            totalPrice = totalPrice.add(lineTotal);

            items.add(item);
        }

        order.setItems(items);
        order.setTotalPrice(totalPrice);

        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    /**
     * Получить заказ по id (для авторизованных).
     */
    @Override
    public OrderDto getById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        return orderMapper.toDto(order);
    }

    /**
     * Мои заказы (по токену).
     */
    @Override
    public Page<OrderDto> getMyOrders(Pageable pageable) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return orderRepository.findAllByUserId(user.getId(), pageable)
                .map(orderMapper::toDto);
    }

    /**
     * Все заказы (для админа).
     */
    @Override
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
                .map(orderMapper::toDto);
    }
    /**
     * Обновление статуса заказа (только для админа).
     */
    @Override
    public OrderDto updateStatus(Long id, OrderStatus status) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

}
