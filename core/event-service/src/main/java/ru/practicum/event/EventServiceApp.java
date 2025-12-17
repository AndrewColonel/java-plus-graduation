package ru.practicum.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import ru.practicum.client.CollectorClient;
import ru.practicum.client.RecomendationsClient;
import ru.practicum.common.JacksonConfig;
import ru.practicum.event.client.category.CategoryClient;
import ru.practicum.event.client.request.RequestClient;
import ru.practicum.event.client.user.UserClient;
import ru.practicum.exception.ApiExceptionHandler;
import ru.practicum.logging.LoggingAspect;


@SpringBootApplication
@Import({ApiExceptionHandler.class,
        LoggingAspect.class,
        CollectorClient.class,
        RecomendationsClient.class,
        JacksonConfig.class})
@EnableFeignClients(clients = {
        UserClient.class,
        CategoryClient.class,
        RequestClient.class})
public class EventServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EventServiceApp.class, args);
    }
}
