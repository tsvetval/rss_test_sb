package ru.rss.aggregator.service.test;

import com.rometools.rome.io.FeedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.service.impl.AggregatorServiceImpl;
import ru.rss.aggregator.service.impl.SimpleFeedConfiguration;
import ru.rss.aggregator.service.mapper.RssFeedMapper;
import ru.rss.aggregator.service.rss.RssReader;
import ru.rss.aggregator.service.storage.RssRepository;
import ru.rss.search.port.SearchService;

import java.io.IOException;

import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// TODO now it's just example, add test
@Test
public class AggregatorServiceTest {
    private static Logger log = LoggerFactory.getLogger(AggregatorServiceTest.class);
    @Mock
    private RssReader rssReader;
    @Mock
    private RssRepository rssRepository;
    @Mock
    private SearchService searchService;


    @BeforeMethod
    public void beforeTest() throws IOException, FeedException {
        MockitoAnnotations.initMocks(this);

        RssFeed testFeed = new RssFeed();
        testFeed.setDescription("test desc");
        testFeed.setTitle("test title");

        when(rssReader.readRss(any())).thenReturn(singletonList(testFeed));
        when(rssRepository.getLatestFeedItem()).thenReturn(null);
    }


    @Test
    public void test() {
        AggregatorServiceImpl aggregatorService = new AggregatorServiceImpl(rssReader, rssRepository, new SimpleFeedConfiguration("url.test.rss"), searchService, new RssFeedMapper());
        aggregatorService.runGrabTask();
        Mockito.verify(rssRepository, Mockito.times(1));
    }

}
