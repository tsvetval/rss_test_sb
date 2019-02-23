package ru.rss.aggregator.service.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.service.repository.mapper.RssMapper;
import ru.rss.aggregator.service.repository.model.RssItem;
import ru.rss.aggregator.service.storage.RssRepository;

import java.util.Collection;
import java.util.List;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Component
@Transactional
public class RssRepositoryImpl implements RssRepository {
    @Autowired
    private RssItemRepository rssItemRepository;
    @Autowired
    private RssMapper rssMapper;

    @Override
    public RssFeed create(RssFeed rssFeed) {
        RssItem rssItem = new RssItem();
        rssItem.setRssFeedModel(rssMapper.toRssFeedModel(rssFeed));
        rssItemRepository.save(rssItem);
        rssFeed.setId(rssItem.getId());
        return rssFeed;
    }

    @Override
    public RssFeed getLastFeedItem() {
        RssItem rssItem = rssItemRepository.getLastFeedItem();
        return rssItem == null ? null : rssMapper.toRssFeed(rssItem.getRssFeedModel());
    }

    @Override
    public Collection<RssFeed> findByIds(Collection<Long> ids) {
        return StreamSupport.stream(Spliterators
                .spliteratorUnknownSize(rssItemRepository.findAllById(ids).iterator(), 0), false)
                .map(rss -> rssMapper.toRssFeed(rss.getRssFeedModel()))
                .collect(Collectors.toList());
    }
}
