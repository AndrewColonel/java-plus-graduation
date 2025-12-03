package ru.practicum.comment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.comment.client.event.EventClient;
import ru.practicum.comment.client.user.UserClient;

@SpringBootApplication
@EnableFeignClients(clients = {UserClient.class, EventClient.class})
public class CommentServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CommentServiceApp.class, args);
    }
}
