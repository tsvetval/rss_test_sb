package ru.rss.aggregator.entity;

import java.io.Serializable;

public class RssFeedChannel implements Serializable {
    private String url;

    public RssFeedChannel(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
