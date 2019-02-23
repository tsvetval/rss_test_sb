package ru.rss.search.repository;

import ru.rss.search.elasticsearch.IndexRepository;
import ru.rss.search.entity.SearchRssFeedInternal;

public interface RssFeedIndexRepository extends IndexRepository<SearchRssFeedInternal> {

    public SearchRssFeedInternal fullTextSearch(String term);

    @Override
    SearchRssFeedInternal save(SearchRssFeedInternal entity);
}
