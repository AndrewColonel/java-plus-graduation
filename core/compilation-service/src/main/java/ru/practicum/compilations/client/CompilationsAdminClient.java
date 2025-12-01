package ru.practicum.compilations.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.practicum.compilations.controller.AdminCompilationControllerOperations;

@FeignClient(name = "compilation-service", path = "/admin/compilations", fallback = CompilationsAdminClientFallback.class)
public interface CompilationsAdminClient extends AdminCompilationControllerOperations {
}
