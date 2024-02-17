package com.example.orderservice.service.impl;

import com.example.orderservice.dto.InventoryResponse;
import com.example.orderservice.dto.OrderLineItemRequest;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.event.InventoryEvent;
import com.example.orderservice.event.OrderPlacedEvent;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItem;
import com.example.orderservice.repository.OrderRepository;
import com.example.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaOrderTemplate;
    private final KafkaTemplate<String, InventoryEvent> kafkaInventoryTemplate;

    @Override
    @Transactional
    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setOrderLineItems(createOrderLineItem(orderRequest.getOrderLineItems()));
        List<String> skuCodes = getSkuCodes(orderRequest.getOrderLineItems());

        if (hasProductsInStock(skuCodes)) {
            orderRepository.save(order);
            kafkaOrderTemplate.send("notificationTopic", new OrderPlacedEvent(order.getOrderNumber()));
            kafkaInventoryTemplate.send("inventoryTopic", new InventoryEvent(skuCodes));
            return "Order saved successfully";
        } else {
            throw new IllegalArgumentException("Product is not in stock, please try again later");
        }
    }

    private static List<OrderLineItem> createOrderLineItem(List<OrderLineItemRequest> orderLineItemRequests) {
        return orderLineItemRequests.stream()
                .map(orderLineItemRequest -> {
                    OrderLineItem orderLineItem = new OrderLineItem();
                    orderLineItem.setSkuCode(orderLineItemRequest.getSkuCode());
                    orderLineItem.setPrice(orderLineItemRequest.getPrice());
                    orderLineItem.setQuantity(orderLineItemRequest.getQuantity());
                    return orderLineItem;
                })
                .collect(Collectors.toList());
    }

    private boolean hasProductsInStock(List<String> skuCodes) {
        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                .uri("http://inventory-service/api/v1/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCodes", skuCodes).build()
                )
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();
        return Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
    }

    private List<String> getSkuCodes(List<OrderLineItemRequest> orderLineItems) {
        return orderLineItems.stream()
                .map(OrderLineItemRequest::getSkuCode)
                .toList();
    }
}
