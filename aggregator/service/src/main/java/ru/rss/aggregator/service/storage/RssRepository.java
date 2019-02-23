package ru.rss.aggregator.service.storage;

import ru.rss.aggregator.entity.RssFeed;

import java.util.List;

public interface RssRepository {
    void create(RssFeed rssFeed);

    List<RssFeed> findAll();

    RssFeed getLastFeedItem();

}
