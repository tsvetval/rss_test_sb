package ru.rss.aggregator.service.storage;

import ru.rss.aggregator.entity.RssFeed;

import java.util.Collection;

public interface RssRepository {

    RssFeed create(RssFeed rssFeed);

    RssFeed getLatestFeedItem();

    Collection<RssFeed> findByIds(Collection<Long> ids);
}
