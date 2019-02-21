package ru.rss.aggregator.port;

import ru.rss.aggregator.entity.RssFeedChannel;

import java.util.List;

public interface RssChannelConfiguration {

    void initRssChannels(List<RssFeedChannel> rssFeedChannels);

    List<RssFeedChannel> getFeedsChannels();
}
