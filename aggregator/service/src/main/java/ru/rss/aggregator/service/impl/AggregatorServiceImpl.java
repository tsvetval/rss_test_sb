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
import ru.rss.aggregator.service.rss.RssReader;
import ru.rss.aggregator.service.storage.RssRepository;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;

@Component
public class AggregatorServiceImpl implements AggregatorService {
    private final Logger logger = LoggerFactory.getLogger(AggregatorServiceImpl.class);
    @Autowired
    private RssReader rssReader;
    @Autowired
    private RssRepository rssRepository;
    @Autowired
    private RssChannelConfiguration rssChannelConfiguration;

    //@Autowired
    //private SearchService searchService;
    @Override
    public void runGrabTask() {
        readRss(rssChannelConfiguration.getFeedsChannel());
    }

    @Override
    public Collection<RssFeed> findByIds(Collection<Long> ids) {
        return rssRepository.findByIds(ids);
    }

    private void readRss(RssFeedChannel rssFeedChannel) {
        try {
            List<RssFeed> rssFeeds = rssReader.readRss(rssFeedChannel);
            RssFeed rssFeed = rssRepository.getLastFeedItem();
            ZonedDateTime maxDateInStorage = rssFeed == null ? ZonedDateTime.now().minus(1L, ChronoUnit.DAYS) : rssFeed.getDate();
            rssFeeds.forEach(o -> {
                if(maxDateInStorage.isBefore(o.getDate())) {
                    rssRepository.create(o);
                    //          searchService.addToSearch(null /*TODO*/);
                }
            });
        } catch (IOException | FeedException e) {
            logger.error("Error on read rss ", e);
        }
    }


}
