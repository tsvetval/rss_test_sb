package ru.rss.aggregator.service.repository.model;

import java.io.Serializable;

public class RssEnclosureModel implements Serializable {
    private String contentType;
    private String url;

    public RssEnclosureModel() {
    }

    public RssEnclosureModel(String contentType, String url) {
        this.contentType = contentType;
        this.url = url;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
