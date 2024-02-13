package com.example.inventoryservice.mapper;

import com.example.inventoryservice.dto.InventoryResponse;
import com.example.inventoryservice.model.Inventory;

import java.util.List;

public class InventoryMapper {

    public static List<InventoryResponse> toInventoryResponses(List<Inventory> inventories) {
        return inventories.stream()
                .map(inventory -> InventoryResponse
                        .builder()
                        .id(inventory.getId())
                        .skuCode(inventory.getSkuCode())
                        .isInStock(inventory.getQuantity() > 0)
                        .build())
                .toList();
    }
}
