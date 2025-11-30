package ru.practicum.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(ru.yandex.practicum.commerce.iteraction.api.logging.Loggable)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Запрос в контроллер: {}", joinPoint.getSignature().getName());

        // логируем параметры запроса
        Object[] args = joinPoint.getArgs();
        log.info("Параметры запроса: {}", args);
        // Выполняется обработка запроса
        Object result = joinPoint.proceed();

        // Логируем ответ
        log.info("Выполненый метод: {} - Результат: {}", joinPoint.getSignature().getName(), result);

        return result;
    }
}