package ru.rss.aggregator.service.storage;

import org.springframework.stereotype.Repository;
import ru.rss.aggregator.entity.RssFeed;

import java.time.ZonedDateTime;
import java.util.List;

public interface RssRepository {
    void create(RssFeed rssFeed);

    List<RssFeed> findAll();

    RssFeed getLastFeedItem();

}
