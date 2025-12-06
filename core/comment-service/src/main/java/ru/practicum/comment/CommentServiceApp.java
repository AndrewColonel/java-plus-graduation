package ru.practicum.comment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import ru.practicum.comment.client.event.EventClient;
import ru.practicum.comment.client.user.UserClient;
import ru.practicum.common.JacksonConfig;
import ru.practicum.exception.ApiExceptionHandler;
import ru.practicum.logging.LoggingAspect;

@SpringBootApplication
@Import({ApiExceptionHandler.class, LoggingAspect.class, JacksonConfig.class})
@EnableFeignClients(clients = {UserClient.class, EventClient.class})
public class CommentServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CommentServiceApp.class, args);
    }
}
