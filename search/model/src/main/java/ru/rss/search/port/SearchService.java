package ru.rss.search.port;

import ru.rss.search.entity.SearchResult;
import ru.rss.search.entity.SearchRssFeed;

public interface SearchService {

    void addToSearch(SearchRssFeed searchRssFeed);

    SearchResult search(String term);

}
