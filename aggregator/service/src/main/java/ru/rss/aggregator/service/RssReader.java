package ru.rss.aggregator.service;

import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.entity.RssFeedChannel;

import java.util.List;

public interface RssReader {
    public List<RssFeed> readRss(RssFeedChannel channel);
}
