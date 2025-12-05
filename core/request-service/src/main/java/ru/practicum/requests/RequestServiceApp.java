package ru.practicum.requests;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import ru.practicum.common.JacksonConfig;
import ru.practicum.exception.ApiExceptionHandler;
import ru.practicum.logging.LoggingAspect;
import ru.practicum.requests.client.event.EventClient;
import ru.practicum.requests.client.user.UserClient;

@SpringBootApplication
@Import({ApiExceptionHandler.class, LoggingAspect.class, JacksonConfig.class})
@EnableFeignClients(clients = {UserClient.class, EventClient.class})
public class RequestServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(RequestServiceApp.class, args);
    }
}
