package ru.practicum.comment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.event.client.EventPublicClient;
import ru.practicum.user.client.UserAdminClient;

@SpringBootApplication
@EnableFeignClients(clients = {UserAdminClient.class, EventPublicClient.class})
public class CommentServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CommentServiceApp.class, args);
    }
}
