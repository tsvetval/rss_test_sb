package ru.rss.aggregator.service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.service.repository.model.RssItem;
import ru.rss.aggregator.service.storage.RssRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
@Transactional
public class RssRepositoryImpl implements RssRepository {
    @Autowired
    private RssItemRepository rssItemRepository;

    @Override
    public void create(RssFeed rssFeed) {
        RssItem rssItem = new RssItem();
        rssItem.setRssFeed(rssFeed);
        rssItemRepository.save(rssItem);
    }

    @Override
    public List<RssFeed> findAll() {
        return StreamSupport.stream(Spliterators
                .spliteratorUnknownSize(rssItemRepository.findAll().iterator(), 0),false).map(RssItem::getRssFeed).collect(Collectors.toList());
    }

    @Override
    public RssFeed getLastFeedItem() {
        RssItem rssItem = rssItemRepository.getLastFeedItem();
        return rssItem == null? null : rssItem.getRssFeed();
    }
}
