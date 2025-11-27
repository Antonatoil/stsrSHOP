package com.example.shop.dto.order;

import com.example.shop.entity.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateOrderStatusRequestDto {

    @NotNull
    private OrderStatus status;

    public UpdateOrderStatusRequestDto() {
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
