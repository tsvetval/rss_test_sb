package ru.rss.aggregator.service.repository.model;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "rss_item")
public class RssItem {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    @Type(type = "RssFeedModelType")
    private RssFeedModel rssFeedModel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RssFeedModel getRssFeedModel() {
        return rssFeedModel;
    }

    public void setRssFeedModel(RssFeedModel rssFeedModel) {
        this.rssFeedModel = rssFeedModel;
    }
}
