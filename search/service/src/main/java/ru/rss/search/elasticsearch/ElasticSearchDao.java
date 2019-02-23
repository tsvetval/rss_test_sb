package ru.rss.search.elasticsearch;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ElasticSearchDao extends ElasticIndexBase {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RefreshPolicy daoRefreshPolicy;

    @Autowired
    public ElasticSearchDao(@Value("${es.index.name}") String indexName, String refreshPolicy, RestHighLevelClient client) {
        super(client, indexName);
        this.daoRefreshPolicy = parseRefreshPolicy(refreshPolicy);
    }

    private RefreshPolicy parseRefreshPolicy(String refreshPolicy) {
        RefreshPolicy policy = RefreshPolicy.NONE;
        try {
            policy = RefreshPolicy.parse(refreshPolicy);
        } catch (IllegalArgumentException e) {
            logger.warn("Wrong property set for RefreshPolicy. Using default one.");
        }

        return policy;
    }


    public String index(ElasticIndexRequest request) {
        IndexRequest updateRequest = prepareIndex(request);
        updateRequest.setRefreshPolicy(daoRefreshPolicy);

        String result;
        try {
            result = client.index(updateRequest, RequestOptions.DEFAULT).getId();
        } catch (Exception e) {
            throw new ElasticSearchException("Index operation fails", e);
        }

        try {
            client.indices().refresh(new RefreshRequest(indexName), RequestOptions.DEFAULT);
            return result;
        } catch (IOException e) {
            throw new ElasticSearchException("Failed to refresh index " + indexName, e);
        }
    }


    private IndexRequest prepareIndex(ElasticIndexRequest requests) {
        String json = toJson(requests.getSource());
        IndexRequest indexRequest = new IndexRequest(indexName, requests.getType(), requests.getId());
        indexRequest.opType(DocWriteRequest.OpType.INDEX);
        indexRequest.source(json, XContentType.JSON);
        if (StringUtils.isNotBlank(requests.getRoutingKey())) {
            indexRequest.routing(requests.getRoutingKey());
        }

        return indexRequest;
    }


    public long count(String type, QueryBuilder queryBuilder) {
        SearchRequest searchRequest = Requests.searchRequest(indexName);
        searchRequest.source().query(queryBuilder);
        searchRequest.source().size(0);
        searchRequest.types(type);
        try {
            return client.search(searchRequest, RequestOptions.DEFAULT).getHits().getTotalHits();
        } catch (IOException e) {
            throw new ElasticSearchException("Failed to count", e);
        }
    }

    public <T> List<T> search(String type, QueryBuilder queryBuilder, Class<T> entityClass) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(type);
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.source().query(queryBuilder);
        searchRequest.source().size(1000);
        List<T> result = new ArrayList<>();
        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
            for (SearchHit searchHitFields : response.getHits()) {
                result.add(elasticObjectMapper.readValue(searchHitFields.getSourceAsString(), entityClass));
            }

            return result;
        } catch (IOException e) {
            throw new ElasticSearchException("Failed to search", e);
        }
    }


}