package ru.rss.search.port;

import ru.rss.search.entity.SearchResult;
import ru.rss.search.entity.SearchRssFeed;

import java.util.List;

public interface SearchService {

    void indexRss(SearchRssFeed searchRssFeed);

    SearchResult searchRss(String term);

}
