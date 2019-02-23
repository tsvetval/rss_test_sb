package ru.rss.search.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.rss.search.elasticsearch.ElasticSearchDao;
import ru.rss.search.entity.SearchRssFeedInternal;

import java.util.Optional;

import static ru.rss.search.elasticsearch.ElasticSearchConfig.RSS_FEED_DAO;

@Component
public class RssFeedIndexRepositoryImpl implements RssFeedIndexRepository {
    private static final String ENTITY_TYPE = "rssFeed";
    ElasticSearchDao elasticSearchDao;

    public RssFeedIndexRepositoryImpl(@Qualifier(RSS_FEED_DAO) ElasticSearchDao elasticSearchDao) {
        this.elasticSearchDao = elasticSearchDao;
    }

    @Override
    public Optional<SearchRssFeedInternal> findOne(String entityId) {
        throw new NotImplementedException("method not implemented yet");
    }

    @Override
    public void remove(String entityId) {
        throw new NotImplementedException("method not implemented yet");
    }

    @Override
    public SearchRssFeedInternal fullTextSearch(String term) {
        return null;
    }

    @Override
    public SearchRssFeedInternal save(SearchRssFeedInternal entity) {
        elasticSearchDao.index();
        return entity;
    }


}
