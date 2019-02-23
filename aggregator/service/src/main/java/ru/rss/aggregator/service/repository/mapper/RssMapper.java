package ru.rss.aggregator.service.repository.mapper;

import org.springframework.stereotype.Component;
import ru.rss.aggregator.entity.RssEnclosure;
import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.service.repository.model.RssEnclosureModel;
import ru.rss.aggregator.service.repository.model.RssFeedModel;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;

@Component
public class RssMapper {

    public RssFeed toRssFeed(RssFeedModel rssFeedModel){
        RssFeed rssFeed = new RssFeed();
        rssFeed.setDate(rssFeedModel.getPublishedDate());
        rssFeed.setDescription(rssFeedModel.getDescription());
        rssFeed.setEnclosures(rssFeedModel.getEnclosures().stream().map(this::toRssEnclosure).collect(Collectors.toList()));
        rssFeed.setTitle(rssFeedModel.getTitle());
        rssFeed.setUrl(rssFeedModel.getUrl());
        return rssFeed;
    }

    RssEnclosure toRssEnclosure(RssEnclosureModel rssEnclosureModel){
        return new RssEnclosure(rssEnclosureModel.getContentType(), rssEnclosureModel.getUrl());
    }

    RssEnclosureModel toRssEnclosureModel(RssEnclosure rssEnclosure){
        return new RssEnclosureModel(rssEnclosure.getContentType(), rssEnclosure.getUrl());
    }

    public RssFeedModel toRssFeedModel(RssFeed rssFeed){
        RssFeedModel rssFeedModel = new RssFeedModel();
        rssFeedModel.setPublishedDate(rssFeed.getDate());
        rssFeedModel.setDescription(rssFeed.getDescription());
        rssFeedModel.setEnclosures(rssFeed.getEnclosures().stream().map(this::toRssEnclosureModel).collect(Collectors.toList()));
        rssFeedModel.setTitle(rssFeed.getTitle());
        rssFeedModel.setUrl(rssFeedModel.getUrl());
        rssFeedModel.setCreatedAt(ZonedDateTime.now(ZoneId.systemDefault()));
        return rssFeedModel;
    }


}
