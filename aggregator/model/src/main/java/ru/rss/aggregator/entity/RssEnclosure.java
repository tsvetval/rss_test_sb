package ru.rss.aggregator.entity;

public class RssEnclosure {
    private String contentType;
    private String url;

    public RssEnclosure(String contentType, String url) {
        this.contentType = contentType;
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public String getUrl() {
        return url;
    }
}
