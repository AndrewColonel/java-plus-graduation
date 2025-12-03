package ru.practicum.compilations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import ru.practicum.compilations.client.EventClient;

@SpringBootApplication
@EnableFeignClients(clients = EventClient.class)
public class CompilationServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CompilationServiceApp.class, args);
    }
}
