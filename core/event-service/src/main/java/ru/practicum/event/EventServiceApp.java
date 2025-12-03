package ru.practicum.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.event.client.category.CategoryClient;
import ru.practicum.event.client.request.RequestClient;
import ru.practicum.requests.client.user.UserClient;

@SpringBootApplication
@EnableFeignClients(clients = {UserClient.class, CategoryClient.class,
RequestClient.class})
public class EventServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EventServiceApp.class, args);
    }
}
