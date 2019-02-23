package ru.rss.aggregator.service.repository.model;

import ru.rss.aggregator.entity.RssEnclosure;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

public class RssFeedModel implements Serializable {
    private String title;
    private String description;
    private String url;
    private List<RssEnclosureModel> enclosures;
    private ZonedDateTime publishedDate;
    private ZonedDateTime createdAt;

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

    public List<RssEnclosureModel> getEnclosures() {
        return enclosures;
    }

    public void setEnclosures(List<RssEnclosureModel> enclosures) {
        this.enclosures = enclosures;
    }

    public ZonedDateTime getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(ZonedDateTime publishedDate) {
        this.publishedDate = publishedDate;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
