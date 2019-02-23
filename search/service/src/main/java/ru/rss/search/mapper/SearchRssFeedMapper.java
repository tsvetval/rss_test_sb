package ru.rss.search.mapper;

import org.springframework.stereotype.Component;
import ru.rss.search.repository.model.SearchRssFeed;
import ru.rss.search.repository.model.SearchRssFeedModel;

@Component
public class SearchRssFeedMapper {

    public SearchRssFeedModel fromRssFeed(SearchRssFeed searchRssFeed){
        return new SearchRssFeedModel(searchRssFeed.getId(), searchRssFeed.getTitle(), searchRssFeed.getDescription());
    }

    public SearchRssFeed toRssFeed(SearchRssFeedModel rssFeed){
        return new SearchRssFeed(rssFeed.getId(), rssFeed.getTitle(), rssFeed.getDescription());
    }
}
