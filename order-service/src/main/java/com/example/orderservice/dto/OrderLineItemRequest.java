package com.example.orderservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderLineItemRequest {
    private Long id;
    private String skuCode;
    private BigDecimal price;
    private Long quantity;
}
