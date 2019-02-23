package ru.rss.search.repository;

import org.apache.commons.lang3.NotImplementedException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.rss.search.elasticsearch.ElasticIndexRequest;
import ru.rss.search.elasticsearch.ElasticSearchDao;
import ru.rss.search.repository.model.SearchRssFeedModel;

import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.boolQuery;
import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;
import static ru.rss.search.elasticsearch.ElasticSearchConfiguration.RSS_FEED_DAO;

@Component
public class RssFeedIndexRepositoryImpl implements RssFeedIndexRepository {
    private static final String ENTITY_TYPE = "rssFeed";
    ElasticSearchDao elasticSearchDao;

    public RssFeedIndexRepositoryImpl(@Qualifier(RSS_FEED_DAO) ElasticSearchDao elasticSearchDao) {
        this.elasticSearchDao = elasticSearchDao;
    }

    @Override
    public Optional<SearchRssFeedModel> findOne(String entityId) {
        throw new NotImplementedException("method not implemented yet");
    }

    @Override
    public void remove(String entityId) {
        throw new NotImplementedException("method not implemented yet");
    }

    @Override
    public List<SearchRssFeedModel> fullTextSearch(String term) {
        BoolQueryBuilder queryBuilder = boolQuery();
        queryBuilder.must(multiMatchQuery(term).field("searchText").type(MultiMatchQueryBuilder.Type.PHRASE_PREFIX));
        return elasticSearchDao.search(ENTITY_TYPE, queryBuilder, SearchRssFeedModel.class);
    }

    @Override
    public SearchRssFeedModel save(SearchRssFeedModel entity) {
        elasticSearchDao.index(new ElasticIndexRequest(String.valueOf(entity.getId()), ENTITY_TYPE, entity));
        return entity;
    }


}
