package ru.rss.aggregator.service.rss;

import com.rometools.rome.io.FeedException;
import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.entity.RssFeedChannel;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

public interface RssReader {
    public List<RssFeed> readRss(RssFeedChannel channel) throws IOException, FeedException;
}
