package ru.rss.search.port;

import ru.rss.search.repository.model.SearchResult;
import ru.rss.search.repository.model.SearchRssFeed;

public interface SearchService {

    void addToSearch(SearchRssFeed searchRssFeed);

    SearchResult search(String term);

}
