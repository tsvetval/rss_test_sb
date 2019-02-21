package ru.rss.aggregator.port;

import ru.rss.aggregator.entity.RssFeed;

import java.util.List;

public interface AggregatorService {

    void runGrabTask();

    List<RssFeed> readAll();
}
