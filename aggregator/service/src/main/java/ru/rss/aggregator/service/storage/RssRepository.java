package ru.rss.aggregator.service.storage;

import ru.rss.aggregator.entity.RssFeed;

public interface RssRepository {
    public void create(RssFeed rssFeed);
}
