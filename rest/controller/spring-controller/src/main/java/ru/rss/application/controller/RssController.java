package ru.rss.application.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.port.AggregatorService;
import ru.rss.search.elasticsearch.ElasticSearchConfiguration;
import ru.rss.search.port.SearchService;
import ru.rss.search.repository.model.SearchRssFeed;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api")
public class RssController {
    private static final Logger log = LoggerFactory.getLogger(RssController.class);
    private final AggregatorService aggregatorService;
    private final SearchService searchService;

    @Autowired
    public RssController(AggregatorService aggregatorService, SearchService searchService) {
        this.aggregatorService = aggregatorService;
        this.searchService = searchService;
    }

    @GetMapping("/list")
    public Collection<RssFeed> getRssList(@RequestParam String term) {
        List<Long> ids = searchService.search(term).getSearchRssFeeds().stream().map(SearchRssFeed::getId).collect(Collectors.toList());
        log.trace("For term {} found {} results", term, ids.size());
        return aggregatorService.findByIds(ids);
    }
}
