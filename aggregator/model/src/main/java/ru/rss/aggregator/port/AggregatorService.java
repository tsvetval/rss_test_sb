package ru.rss.aggregator.port;

import ru.rss.aggregator.entity.RssFeed;

import java.util.Collection;
import java.util.List;

public interface AggregatorService {

    void runGrabTask();

    Collection<RssFeed> findByIds(Collection<Long> ids);
}
