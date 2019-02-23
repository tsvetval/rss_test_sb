package ru.rss.aggregator.service.impl;

import ru.rss.aggregator.entity.RssFeedChannel;
import ru.rss.aggregator.port.RssChannelConfiguration;

public class SimpleFeedConfiguration implements RssChannelConfiguration {

    private final RssFeedChannel rssFeedChannel;

    public SimpleFeedConfiguration(String rssUrl) {
        this.rssFeedChannel = new RssFeedChannel(rssUrl);
    }

    @Override
    public RssFeedChannel getFeedsChannel() {
        return rssFeedChannel;
    }
}
