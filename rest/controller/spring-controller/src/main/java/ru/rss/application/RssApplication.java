package ru.rss.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.rss.aggregator.service.AggregatorConfiguration;

@SpringBootApplication
@Import(AggregatorConfiguration.class)
@EnableAutoConfiguration
@ComponentScan
public class RssApplication {

    public static void main(String[] args) {
        SpringApplication.run(RssApplication.class);
    }
}
