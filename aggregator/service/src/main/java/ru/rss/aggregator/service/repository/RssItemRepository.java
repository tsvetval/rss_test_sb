package ru.rss.aggregator.service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.service.repository.model.RssItem;

public interface RssItemRepository extends CrudRepository<RssItem, Long> {

    @Query(value = "SELECT * FROM rss_item r WHERE r.rss_feed_model ->> 'publishedDate' = (SELECT max(e.rss_feed_model ->> 'publishedDate') FROM rss_item e) ",nativeQuery = true)
    RssItem getLastFeedItem();

}
