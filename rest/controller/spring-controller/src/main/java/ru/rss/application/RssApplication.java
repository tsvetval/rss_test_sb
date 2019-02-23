package ru.rss.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.rss.aggregator.service.AggregatorConfiguration;
import ru.rss.search.SearchServiceConfiguration;

@SpringBootApplication
@Import({AggregatorConfiguration.class,SearchServiceConfiguration.class})
@EnableAutoConfiguration
@ComponentScan
public class RssApplication {

    public static void main(String[] args) {
        SpringApplication.run(RssApplication.class);
    }
}
