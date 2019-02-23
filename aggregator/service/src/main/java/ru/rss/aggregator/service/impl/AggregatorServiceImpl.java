package ru.rss.aggregator.service.impl;

import com.rometools.rome.io.FeedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.entity.RssFeedChannel;
import ru.rss.aggregator.port.AggregatorService;
import ru.rss.aggregator.port.RssChannelConfiguration;
import ru.rss.aggregator.service.mapper.RssFeedMapper;
import ru.rss.aggregator.service.rss.RssReader;
import ru.rss.aggregator.service.storage.RssRepository;
import ru.rss.search.port.SearchService;

import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class AggregatorServiceImpl implements AggregatorService {

    private final Logger log = LoggerFactory.getLogger(AggregatorServiceImpl.class);

    private final RssReader rssReader;
    private final RssRepository rssRepository;
    private final RssChannelConfiguration rssChannelConfiguration;
    private final SearchService searchService;
    private final RssFeedMapper rssFeedMapper;

    @Autowired
    public AggregatorServiceImpl(RssReader rssReader, RssRepository rssRepository, RssChannelConfiguration rssChannelConfiguration, SearchService searchService, RssFeedMapper rssFeedMapper) {
        this.rssReader = rssReader;
        this.rssRepository = rssRepository;
        this.rssChannelConfiguration = rssChannelConfiguration;
        this.searchService = searchService;
        this.rssFeedMapper = rssFeedMapper;
    }

    @Override
    public void runGrabTask() {
        log.info("Rss read job started, grab rss channel {}", rssChannelConfiguration.getFeedsChannel().getUrl());
        readRss(rssChannelConfiguration.getFeedsChannel());
        log.info("Rss read job finished");
    }

    @Override
    public Collection<RssFeed> findByIds(Collection<Long> ids) {
        return rssRepository.findByIds(ids);
    }

    private void readRss(RssFeedChannel rssFeedChannel) {
        try {
            final List<RssFeed> rssFeeds = rssReader.readRss(rssFeedChannel);
            final RssFeed latestRssFeed = rssRepository.getLatestFeedItem();
            final ZonedDateTime maxDateInStorage = Objects.nonNull(latestRssFeed) ? latestRssFeed.getDate() : null;
            log.info("Grabbed {} feeds", rssFeeds.size());
            AtomicInteger updatedCount = new AtomicInteger();
            rssFeeds.forEach(o -> {
                if (Objects.isNull(o.getDate()) || Objects.isNull(maxDateInStorage) || o.getDate().isAfter(maxDateInStorage)) {
                    rssRepository.create(o);
                    log.trace("Created new RssFeed with id {}", o.getId());
                    searchService.addToSearch(rssFeedMapper.toSearchRssFeed(o));
                    updatedCount.getAndIncrement();
                }
            });
            log.info("Created {} feeds", updatedCount);
        } catch (IOException | FeedException e) {
            log.error("Error while reading rss ", e);
        }
    }


}
