package ru.rss.search.entity;

public class SearchRssFeedInternal {
    private String title;
    private String content;

    public SearchRssFeedInternal(String title, String feedSource) {
        this.title = title;
        this.content = feedSource;
    }
}
