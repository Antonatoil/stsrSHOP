package com.example.shop.repository;

import com.example.shop.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {


    @Override
    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    Page<Order> findAll(Pageable pageable);


    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    Page<Order> findAllByUserId(Long userId, Pageable pageable);


    @Override
    @EntityGraph(attributePaths = {"items", "items.product", "user"})
    Optional<Order> findById(Long id);
}
