package ru.rss.search.indicator;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Component;

import static org.springframework.boot.actuate.health.Health.status;

@Component
public class ElasticSearchHealthIndicator implements HealthIndicator {

    private final RestHighLevelClient restHighLevelClient;

    @Autowired
    public ElasticSearchHealthIndicator(RestHighLevelClient restHighLevelClient) {
        this.restHighLevelClient = restHighLevelClient;
    }

    @Override
    public Health health() {
        Health.Builder healthBuilder = null;
        try {
            if (restHighLevelClient.ping(RequestOptions.DEFAULT)) {
                healthBuilder = status(Status.UP);
            } else {
                healthBuilder = status(Status.DOWN);
            }
        } catch (Exception e) {
            healthBuilder = status(Status.DOWN).withException(e);
        }
        return healthBuilder.build();

    }
}
