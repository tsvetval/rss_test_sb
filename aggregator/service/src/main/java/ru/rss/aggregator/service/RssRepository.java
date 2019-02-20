package ru.rss.aggregator.service;

import ru.rss.aggregator.entity.RssFeed;

public interface RssRepository {
    public void create(RssFeed rssFeed);
}
