package ru.rss.aggregator.service.impl;

import com.rometools.rome.io.FeedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.entity.RssFeedChannel;
import ru.rss.aggregator.port.AggregatorService;
import ru.rss.aggregator.port.RssChannelConfiguration;
import ru.rss.aggregator.service.RssReader;
import ru.rss.aggregator.service.RssRepository;
import ru.rss.search.port.SearchService;

import java.io.IOException;
import java.util.List;

public class AggregatorServiceImpl implements AggregatorService {
    Logger logger = LoggerFactory.getLogger(AggregatorServiceImpl.class);
    private RssReader rssReader;
    private RssRepository rssRepository;
    private RssChannelConfiguration rssChannelConfiguration;
    private SearchService searchService;

    @Override
    public void runGrabTask() {
        rssChannelConfiguration.getFeedsChannels().forEach(this::readRss);
    }


    private void readRss(RssFeedChannel rssFeedChannel) {
        try {
            List<RssFeed> rssFeeds = rssReader.readRss(rssFeedChannel);

            rssFeeds.forEach(o -> {
                rssRepository.create(o);
                searchService.indexRss(null /*TODO*/);
            });
        } catch (IOException | FeedException e) {
            logger.error("Error on read rss ", e);
        }
    }

}
