package ru.rss.aggregator.service.rss;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.springframework.stereotype.Component;
import ru.rss.aggregator.entity.RssEnclosure;
import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.entity.RssFeedChannel;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class RomeRssReaderImpl implements RssReader {

    @Override
    public List<RssFeed> readRss(RssFeedChannel channel) throws IOException, FeedException {
        URL feedUrl = new URL(channel.getUrl());
        SyndFeedInput input = new SyndFeedInput();
        SyndFeed feed = input.build(new XmlReader(feedUrl));
        return feed.getEntries().stream().map(this::toRssFeed).collect(Collectors.toList());
    }

    private RssFeed toRssFeed(SyndEntry syndEntry) {
        return new RssFeed(syndEntry.getTitle(),
                syndEntry.getDescription() == null ? "" : syndEntry.getDescription().getValue(),
                syndEntry.getUri(),
                syndEntry.getEnclosures().stream().map(enc -> new RssEnclosure(enc.getType(), enc.getUrl())).collect(Collectors.toList()),
                ZonedDateTime.ofInstant(syndEntry.getPublishedDate().toInstant(), ZoneId.of("UTC")));
    }
}
