package ru.rss.search.mapper;

import org.springframework.stereotype.Component;
import ru.rss.search.entity.SearchRssFeed;
import ru.rss.search.entity.SearchRssFeedInternal;

@Component
public class RssFeedMapper {

    public SearchRssFeedInternal fromRssFeed(SearchRssFeed searchRssFeed){
        return new SearchRssFeedInternal(searchRssFeed.getTitle(), searchRssFeed.getContent());
    }
}
