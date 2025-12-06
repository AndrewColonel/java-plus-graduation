package ru.practicum.category;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import ru.practicum.category.client.event.EventClient;
import ru.practicum.common.JacksonConfig;
import ru.practicum.exception.ApiExceptionHandler;
import ru.practicum.logging.LoggingAspect;

@SpringBootApplication
@Import({ApiExceptionHandler.class, LoggingAspect.class, JacksonConfig.class})
@EnableFeignClients(clients = {EventClient.class})
public class CategoryServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CategoryServiceApp.class, args);
    }
}
