package ru.rss.aggregator.entity;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class RssFeed implements Serializable {
    private String title;
    private String description;
    private String url;
    private List<RssEnclosure> enclosures;
    private ZonedDateTime date;
    private String feedSource;

    public RssFeed() {
    }

    public RssFeed(String title, String description, String url, List<RssEnclosure> enclosures, ZonedDateTime date) {
        this.title = title;
        this.description = description;
        this.url = url;
        this.enclosures = enclosures;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<RssEnclosure> getEnclosures() {
        return enclosures;
    }

    public void setEnclosures(List<RssEnclosure> enclosures) {
        this.enclosures = enclosures;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
    }
}
