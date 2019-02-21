package ru.rss.aggregator.entity;

public class RssFeedChannel {
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
