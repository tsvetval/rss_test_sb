package ru.rss.application;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.Scheduled;
import ru.rss.aggregator.port.AggregatorService;
import ru.rss.aggregator.service.AggregatorConfiguration;

@SpringBootApplication
@Import(AggregatorConfiguration.class)
@EnableAutoConfiguration
public class RssApplication  {

    public static void main(String[] args) {
        SpringApplication.run(RssApplication.class);
    }
}
