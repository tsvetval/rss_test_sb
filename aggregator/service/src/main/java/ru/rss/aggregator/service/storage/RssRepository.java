package ru.rss.aggregator.service.storage;

import ru.rss.aggregator.entity.RssFeed;

import java.util.Collection;
import java.util.List;

public interface RssRepository {
    RssFeed create(RssFeed rssFeed);

    RssFeed getLastFeedItem();

    Collection<RssFeed> findByIds(Collection<Long> ids);
}
