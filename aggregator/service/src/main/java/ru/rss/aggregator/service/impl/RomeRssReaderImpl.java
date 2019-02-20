package ru.rss.aggregator.service.impl;

import com.rometools.rome.feed.synd.SyndEnclosure;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.rss.aggregator.entity.RssEnclosure;
import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.entity.RssFeedChannel;
import ru.rss.aggregator.service.RssReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RomeRssReaderImpl implements RssReader {

    @Override
    public List<RssFeed> readRss(RssFeedChannel channel) throws IOException, FeedException {
        URL feedUrl = new URL("http://static.feed.rbc.ru/rbc/logical/footer/news.rss");
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));
        return feed.getEntries().stream().map(this::toRssFeed).collect(Collectors.toList());
    }

    private RssFeed toRssFeed(SyndEntry syndEntry) {
        return new RssFeed(syndEntry.getTitle(),
                syndEntry.getDescription() == null ? "" : syndEntry.getDescription().getValue(),
                syndEntry.getUri(),
                syndEntry.getEnclosures().stream().map(enc -> new RssEnclosure(enc.getType(), enc.getUrl())).collect(Collectors.toList()),
                ZonedDateTime.now());
    }
}
