package com.example.notificationservice;

import com.example.notificationservice.event.InventoryEvent;
import com.example.notificationservice.event.OrderPlacedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.KafkaListener;

@Slf4j
@SpringBootApplication
public class NotificationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @KafkaListener(topics = "notificationTopic", groupId = "notificationId")
    public void handleOrderNotification(OrderPlacedEvent orderPlacedEvent) {
        log.info("Received Notification for Order - {}", orderPlacedEvent.getOrderNumber());
    }

    @KafkaListener(topics = "inventoryTopic", groupId = "notificationId")
    public void handleInventoryNotification(InventoryEvent inventoryEvent) {
        log.info("Received Notification for Order - {}", inventoryEvent.getSkuCodes());
    }
}
