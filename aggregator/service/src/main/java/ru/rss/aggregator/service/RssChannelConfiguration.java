package ru.rss.aggregator.service;

import ru.rss.aggregator.entity.RssFeedChannel;

import java.util.List;

public interface RssChannelConfiguration {

    List<RssFeedChannel> getFeedsChannels();
}
