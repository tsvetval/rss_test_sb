package ru.rss.aggregator.service.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.rss.aggregator.service.repository.model.RssItem;

public interface RssItemRepository extends CrudRepository<RssItem, Long> {

    @Query(nativeQuery = true,
            value = "SELECT * FROM rss_item r order by r.rss_feed_model ->> 'publishedDate' desc LIMIT 1 OFFSET  0")
    RssItem getLatestFeedItem();

}
