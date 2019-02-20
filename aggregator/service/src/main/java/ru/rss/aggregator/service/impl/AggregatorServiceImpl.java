package ru.rss.aggregator.service.impl;

import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.entity.RssFeedChannel;
import ru.rss.aggregator.port.AggregatorService;
import ru.rss.aggregator.service.RssChannelConfiguration;
import ru.rss.aggregator.service.RssReader;
import ru.rss.aggregator.service.RssRepository;
import ru.rss.search.port.SearchService;

import java.util.List;

public class AggregatorServiceImpl implements AggregatorService {

    private RssReader rssReader;
    private RssRepository rssRepository;
    private RssChannelConfiguration rssChannelConfiguration;
    private SearchService searchService;

    @Override
    public void runGrabTask() {
        rssChannelConfiguration.getFeedsChannels().forEach(this::readRss);
    }


    private void readRss(RssFeedChannel rssFeedChannel) {
        List<RssFeed> rssFeeds = rssReader.readRss(rssFeedChannel);

        rssFeeds.forEach(o -> {
            rssRepository.create(o);
            searchService.indexRss(null /*TODO*/);
        });
    }

}
