package ru.rss.aggregator.service.impl;

import org.springframework.stereotype.Component;
import ru.rss.aggregator.entity.RssFeedChannel;
import ru.rss.aggregator.port.RssChannelConfiguration;

import java.util.Collections;
import java.util.List;

@Component
public class SimpleFeedConfiguration implements RssChannelConfiguration {


    @Override
    public void initRssChannels(List<RssFeedChannel> rssFeedChannels) {

    }

    @Override
    public List<RssFeedChannel> getFeedsChannels() {
        return Collections.singletonList(new RssFeedChannel("https://lenta.ru/rss"));
    }
}
