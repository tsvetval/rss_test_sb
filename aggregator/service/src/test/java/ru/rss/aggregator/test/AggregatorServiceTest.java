package ru.rss.aggregator.test;

import com.rometools.rome.io.FeedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import ru.rss.aggregator.entity.RssFeedChannel;
import ru.rss.aggregator.service.rss.RomeRssReaderImpl;
import ru.rss.aggregator.service.rss.RssReader;

import java.io.IOException;

@Test
public class AggregatorServiceTest {
    private static Logger log = LoggerFactory.getLogger(AggregatorServiceTest.class);

    @Test
    public void test() throws IOException, FeedException {
        RssReader rssReader = new RomeRssReaderImpl();
        rssReader.readRss(new RssFeedChannel("http://static.feed.rbc.ru/rbc/logical/footer/news.rss"))
                .forEach(rssFeed -> log.info("feed {}, {}, {}, {}, {}", rssFeed.getTitle(), rssFeed.getDescription(), rssFeed.getUrl(), rssFeed.getEnclosures()));

    }

}