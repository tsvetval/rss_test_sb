package ru.rss.aggregator.service.mapper;

import org.springframework.stereotype.Component;
import ru.rss.aggregator.entity.RssFeed;
import ru.rss.search.repository.model.SearchRssFeed;

@Component
public class RssFeedMapper {

    public SearchRssFeed toSearchRssFeed(RssFeed rssFeed){
        return new SearchRssFeed(rssFeed.getId(), rssFeed.getTitle(), rssFeed.getDescription());
    }

}
