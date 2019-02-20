package ru.rss.search.port;

import ru.rss.search.entity.SearchResult;
import ru.rss.search.entity.SearchRssFeed;

import java.util.List;

public interface SearchService {
    public void indexRss(SearchRssFeed searchRssFeed);

    public SearchResult searchRss(String term);

}
