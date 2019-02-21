package ru.rss.aggregator.service.repository.model;

import org.hibernate.annotations.Type;
import ru.rss.aggregator.entity.RssFeed;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "rss_item")
public class RssItem {
    @Id
    @GeneratedValue
    private Long id;
    @Column
    @Type(type = "RssFeedType")
    private RssFeed rssFeed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RssFeed getRssFeed() {
        return rssFeed;
    }

    public void setRssFeed(RssFeed rssFeed) {
        this.rssFeed = rssFeed;
    }
}
