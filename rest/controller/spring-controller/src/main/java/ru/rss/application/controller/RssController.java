package ru.rss.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.port.AggregatorService;
import ru.rss.search.port.SearchService;
import ru.rss.search.repository.model.SearchRssFeed;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
public class RssController {
    @Autowired
    private AggregatorService aggregatorService;
    @Autowired
    private SearchService searchService;

    @GetMapping("/list")
    public Collection<RssFeed> getRssList(@RequestParam String term) {
        List<Long> ids = searchService.search(term).getSearchRssFeeds().stream().map(SearchRssFeed::getId).collect(Collectors.toList());
        return aggregatorService.findByIds(ids);
    }
}
