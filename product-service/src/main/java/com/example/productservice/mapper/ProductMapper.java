package com.example.productservice.mapper;

import com.example.productservice.dto.response.ProductResponse;
import com.example.productservice.model.Product;

import java.util.List;
import java.util.stream.Collectors;

public class ProductMapper {

    public static List<ProductResponse> toProductsResponse(List<Product> products) {
        return products.stream()
                .map(ProductMapper::toProductResponse)
                .collect(Collectors.toList());
    }

    public static ProductResponse toProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();
    }
}
