package com.example.inventoryservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InventoryResponse {
    private Long id;
    private String skuCode;
    private boolean isInStock;
}
