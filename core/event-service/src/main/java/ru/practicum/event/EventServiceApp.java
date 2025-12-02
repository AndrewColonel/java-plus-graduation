package ru.practicum.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.category.client.CategoryPublicClient;
import ru.practicum.requests.client.RequestPrivatClient;
import ru.practicum.user.client.UserAdminClient;

@SpringBootApplication
@EnableFeignClients(clients = {UserAdminClient.class, CategoryPublicClient.class,
RequestPrivatClient.class})
public class EventServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EventServiceApp.class, args);
    }
}
