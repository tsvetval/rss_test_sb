package ru.rss.aggregator.service.impl;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rss.aggregator.entity.RssFeedChannel;
import ru.rss.aggregator.service.RssReader;

import java.io.IOException;
import java.net.URL;

public class TestRome {
    private static Logger log = LoggerFactory.getLogger(AggregatorServiceImpl.class);

    public static void main(String[] args) throws IOException, FeedException {
        boolean ok = false;

            RssReader rssReader = new RomeRssReaderImpl();
            rssReader.readRss(new RssFeedChannel("http://static.feed.rbc.ru/rbc/logical/footer/news.rss"))
                    .forEach(rssFeed -> log.info("feed {}, {}, {}, {}, {}",rssFeed.getTitle(), rssFeed.getDescription(), rssFeed.getUrl(), rssFeed.getEnclosures()));

    }
}
