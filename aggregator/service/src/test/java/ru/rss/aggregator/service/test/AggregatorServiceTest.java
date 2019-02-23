package ru.rss.aggregator.service.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

@Test
public class AggregatorServiceTest {
    private static Logger log = LoggerFactory.getLogger(AggregatorServiceTest.class);

    @Test
    public void test() {
        log.info("test!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        //throw new RuntimeException();
//        RssReader rssReader = new RomeRssReaderImpl();
//        rssReader.readRss(new RssFeedChannel("http://static.feed.rbc.ru/rbc/logical/footer/news.rss"))
//                .forEach(rssFeed -> log.info("feed {}, {}, {}, {}, {}", rssFeed.getTitle(), rssFeed.getDescription(), rssFeed.getUrl(), rssFeed.getEnclosures()));

    }

}
