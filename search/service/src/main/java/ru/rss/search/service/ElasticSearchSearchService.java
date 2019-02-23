package ru.rss.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rss.search.entity.SearchResult;
import ru.rss.search.entity.SearchRssFeed;
import ru.rss.search.mapper.RssFeedMapper;
import ru.rss.search.port.SearchService;
import ru.rss.search.repository.RssFeedIndexRepository;

@Component
public class ElasticSearchSearchService implements SearchService {

    final RssFeedIndexRepository rssFeedIndexRepository;
    final RssFeedMapper rssFeedMapper;

    @Autowired
    public ElasticSearchSearchService(RssFeedIndexRepository rssFeedIndexRepository, RssFeedMapper rssFeedMapper) {
        this.rssFeedIndexRepository = rssFeedIndexRepository;
        this.rssFeedMapper = rssFeedMapper;
    }

    @Override
    public void addToSearch(SearchRssFeed searchRssFeed) {
        rssFeedIndexRepository.save(rssFeedMapper.fromRssFeed(searchRssFeed));
    }

    @Override
    public SearchResult search(String term) {
        rssFeedIndexRepository.fullTextSearch(term);
        return null;
    }
}
