package ru.rss.search.repository.model;

import java.util.ArrayList;
import java.util.List;

public class SearchResult{
    private List<SearchRssFeed> searchRssFeeds  = new ArrayList<>();

    public List<SearchRssFeed> getSearchRssFeeds() {
        return searchRssFeeds;
    }
}
