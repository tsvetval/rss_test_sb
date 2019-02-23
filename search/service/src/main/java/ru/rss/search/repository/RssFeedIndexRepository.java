package ru.rss.search.repository;

import ru.rss.search.elasticsearch.IndexRepository;
import ru.rss.search.repository.model.SearchRssFeedModel;

import java.util.List;

public interface RssFeedIndexRepository extends IndexRepository<SearchRssFeedModel> {

    List<SearchRssFeedModel> fullTextSearch(String term);

    @Override
    SearchRssFeedModel save(SearchRssFeedModel entity);
}
