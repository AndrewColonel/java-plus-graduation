package ru.practicum.aggregator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.service.AggregationStarter;

@Component
@RequiredArgsConstructor
public class AggregatorRunner implements CommandLineRunner {
    final AggregationStarter aggregationStarter;


    @Override
    public void run(String... args) throws Exception {
        aggregationStarter.run();
    }
}
