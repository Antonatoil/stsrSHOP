package com.example.shop.service.impl;

import com.example.shop.dto.order.CreateOrderRequestDto;
import com.example.shop.dto.order.OrderDto;
import com.example.shop.entity.Order;
import com.example.shop.entity.OrderItem;
import com.example.shop.entity.Product;
import com.example.shop.entity.User;
import com.example.shop.entity.enums.OrderStatus;
import com.example.shop.exception.BadRequestException;
import com.example.shop.exception.NotFoundException;
import com.example.shop.mapper.OrderMapper;
import com.example.shop.repository.OrderRepository;
import com.example.shop.repository.ProductRepository;
import com.example.shop.repository.UserRepository;
import com.example.shop.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
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


    @Override
    @Transactional
    public OrderDto create(CreateOrderRequestDto dto) {
        log.info("Создание заказа для текущего пользователя");

        // 1. Достаём e-mail текущего пользователя из SecurityContext
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        log.debug("Текущий пользователь из SecurityContext: email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));
        log.debug("Пользователь найден: id={}, email={}", user.getId(), user.getEmail());

        // 2. Создаём Order
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.NEW);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        BigDecimal totalPrice = BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        dto.getItems().forEach(itemDto -> {
            Long productId = itemDto.getProductId();
            Integer quantity = itemDto.getQuantity();

            log.debug("Обработка позиции заказа: productId={}, quantity={}", productId, quantity);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Product not found"));

            Integer currentStock = product.getStock();
            if (currentStock == null) {
                currentStock = 0;
            }

            if (currentStock < quantity) {
                throw new BadRequestException(
                        "Not enough stock for product id=" + productId +
                                ". Requested=" + quantity + ", available=" + currentStock
                );
            }

            product.setStock(currentStock - quantity);


            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setProduct(product);
            item.setQuantity(quantity);
            item.setPrice(product.getPrice());

            items.add(item);
        });

        for (OrderItem item : items) {
            BigDecimal lineTotal = item.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(lineTotal);
        }

        order.setItems(items);
        order.setTotalPrice(totalPrice);

        Order saved = orderRepository.save(order);
        log.info("Заказ создан: id={}, userId={}, totalPrice={}",
                saved.getId(), user.getId(), saved.getTotalPrice());

        return orderMapper.toDto(saved);
    }


    @Override
    public OrderDto getById(Long id) {
        log.debug("Поиск заказа по id={}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));
        return orderMapper.toDto(order);
    }

    @Override
    public Page<OrderDto> getMyOrders(Pageable pageable) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
        log.debug("Получение заказов для текущего пользователя: email={}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        log.debug("Поиск заказов по userId={} с page={}, size={}",
                user.getId(), pageable.getPageNumber(), pageable.getPageSize());

        Page<Order> ordersPage = orderRepository.findAllByUserId(user.getId(), pageable);

        // Маппим в DTO прямо тут, пока транзакция и сессия ещё живы
        List<OrderDto> dtoList = ordersPage
                .getContent()
                .stream()
                .map(orderMapper::toDto)
                .toList();

        return new PageImpl<>(dtoList, pageable, ordersPage.getTotalElements());
    }


    @Override
    public Page<OrderDto> getAllOrders(Pageable pageable) {
        log.info("Получение всех заказов: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<Order> ordersPage = orderRepository.findAll(pageable);

        List<OrderDto> dtoList = ordersPage
                .getContent()
                .stream()
                .map(orderMapper::toDto)
                .toList();

        return new PageImpl<>(dtoList, pageable, ordersPage.getTotalElements());
    }

    @Override
    @Transactional
    public OrderDto updateStatus(Long id, OrderStatus status) {
        log.info("Обновление статуса заказа id={} на {}", id, status);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Order not found"));

        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);
        log.info("Статус заказа обновлён: id={}, status={}", saved.getId(), saved.getStatus());
        return orderMapper.toDto(saved);
    }
}
