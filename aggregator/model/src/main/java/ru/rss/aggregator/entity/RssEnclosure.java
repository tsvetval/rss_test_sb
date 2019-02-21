package ru.rss.aggregator.entity;

import java.io.Serializable;

public class RssEnclosure implements Serializable {
    private String contentType;
    private String url;

    public RssEnclosure() {
    }

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
