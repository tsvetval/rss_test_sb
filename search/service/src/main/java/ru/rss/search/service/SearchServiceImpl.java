package ru.rss.search.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.rss.search.mapper.SearchRssFeedMapper;
import ru.rss.search.port.SearchService;
import ru.rss.search.repository.RssFeedIndexRepository;
import ru.rss.search.repository.model.SearchResult;
import ru.rss.search.repository.model.SearchRssFeed;
import ru.rss.search.repository.model.SearchRssFeedModel;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SearchServiceImpl implements SearchService {

    final RssFeedIndexRepository rssFeedIndexRepository;
    final SearchRssFeedMapper searchRssFeedMapper;

    @Autowired
    public SearchServiceImpl(RssFeedIndexRepository rssFeedIndexRepository, SearchRssFeedMapper searchRssFeedMapper) {
        this.rssFeedIndexRepository = rssFeedIndexRepository;
        this.searchRssFeedMapper = searchRssFeedMapper;
    }

    @Override
    public void addToSearch(SearchRssFeed searchRssFeed) {
        rssFeedIndexRepository.save(searchRssFeedMapper.fromRssFeed(searchRssFeed));
    }

    @Override
    public SearchResult search(String term) {
        List<SearchRssFeedModel> searchRssFeedModels = rssFeedIndexRepository.fullTextSearch(term);
        SearchResult searchResult = new SearchResult();
        searchResult.getSearchRssFeeds().addAll(searchRssFeedModels.stream().map(searchRssFeedMapper::toRssFeed).collect(Collectors.toList()));
        return searchResult;
    }
}
