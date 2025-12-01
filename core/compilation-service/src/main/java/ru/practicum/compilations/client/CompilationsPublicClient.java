package ru.practicum.compilations.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.compilations.controller.PublicCompilationControllerOperations;

@FeignClient(name = "compilation-service", path = "/compilations", fallback = CompilationsPublicClientFallback.class)
public interface CompilationsPublicClient extends PublicCompilationControllerOperations {
}
