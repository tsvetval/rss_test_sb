package ru.rss.aggregator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import ru.rss.aggregator.port.AggregatorService;
import ru.rss.aggregator.service.repository.model.RssItem;

@Configuration
@ComponentScan
@EnableJpaRepositories
@EntityScan(basePackageClasses = RssItem.class)
@EnableScheduling
public class AggregatorConfiguration {


    @Autowired
    private AggregatorService aggregatorService;

    @Scheduled(fixedDelayString = "${rss.aggregator.job.delay:30000}")
    public void reedFeeds() {
        aggregatorService.runGrabTask();
    }


}
