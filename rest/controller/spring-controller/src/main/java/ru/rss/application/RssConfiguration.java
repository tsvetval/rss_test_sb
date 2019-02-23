package ru.rss.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.rss.aggregator.port.RssChannelConfiguration;
import ru.rss.aggregator.service.impl.SimpleFeedConfiguration;

@Configuration
public class RssConfiguration {
    @Value("${rss.channel}")
    private String rssUrl;

    @Bean
    public RssChannelConfiguration rssChannelConfiguration(){
        return new SimpleFeedConfiguration(rssUrl);
    }

}
