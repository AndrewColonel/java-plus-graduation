package ru.practicum.analyzer;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.service.EventSimilarityProcessor;
import ru.practicum.analyzer.service.UserActionProcessor;


@Component
@RequiredArgsConstructor
public class AnalyserRunner implements CommandLineRunner {
    final UserActionProcessor userActionProcessor;
    final EventSimilarityProcessor eventSimilarityProcessor;

    @Override
    public void run(String... args) throws Exception {

        Thread userActionThread = new Thread(userActionProcessor);
        userActionThread.setName("UserActionThread");
        userActionThread.start();

        eventSimilarityProcessor.run();

    }
}
