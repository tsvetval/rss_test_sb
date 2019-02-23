package ru.rss.search.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.zoetis.ngp.JsonUtils;
import com.zoetis.ngp.dataindexer.dto.BaseIndexedDto;
import com.zoetis.ngp.dataindexer.schema.CdcbAnimalOrderSchema;
import com.zoetis.ngp.error.data.ErrorCode;
import com.zoetis.ngp.error.exception.NGPException;
import com.zoetis.ngp.paging.BulkIterator;
import javaslang.collection.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.WriteRequest.RefreshPolicy;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.sort.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoZonedDateTime;
import java.util.*;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class ElasticSearchDao extends ElasticSearchBaseDao{

    public static final int DEFAULT_SCROLL_KEEP_ALIVE_IN_MS = 60000;
    private static final String FAILED_TO_SEARCH_MESSAGE = "Failed to search";






    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RefreshPolicy daoRefreshPolicy;

    private final int keepScrollAliveInMs;


    @Autowired
    public ElasticSearchDao(@Value("${es.index.name}") String indexName, String refreshPolicy, RestHighLevelClient client) {
        this(indexName, refreshPolicy, DEFAULT_SCROLL_KEEP_ALIVE_IN_MS, client);
    }

    public ElasticSearchDao(String indexName, String refreshPolicy, int keepScrollAliveInMs, RestHighLevelClient client) {
        super(client);
        this.indexName = indexName;


        this.elasticObjectMapper = JsonUtils.newObjectMapper()
                .setSerializationInclusion(JsonInclude.Include.ALWAYS)
                .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false);

        this.daoRefreshPolicy = parseRefreshPolicy(refreshPolicy);

        this.keepScrollAliveInMs = keepScrollAliveInMs;
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

    @PostConstruct
    public void init() {
        init(false);
    }

    public String index(NgpElasticIndexRequest request) {
        IndexRequest updateRequest = prepareIndex(request);
        updateRequest.setRefreshPolicy(daoRefreshPolicy);

        String result;
        try {
            result = client.index(updateRequest, RequestOptions.DEFAULT).getId();
        } catch (Exception e) {
            throw new ElasticSearchException(FAILED_TO_SEND_UPDATE_REQUEST_MESSAGE, e);
        }

        try {
            client.indices().refresh(new RefreshRequest(indexName), RequestOptions.DEFAULT);
            return result;
        } catch (IOException e) {
            throw new ElasticSearchException("Failed to refresh index " + indexName, e);
        }
    }


    private IndexRequest prepareIndex(NgpElasticIndexRequest requests) {
        String json = toJson(requests.getSource());

        /*
         * Here we use IndexRequestBuilder with DocWriteRequest.OpType.INDEX to reindex all nested document fields
         * for UpdateRequest remove operation on collection does not work
         */
        IndexRequest indexRequest = new IndexRequest(indexName, requests.getType(), requests.getId());
        indexRequest.opType(DocWriteRequest.OpType.INDEX);
        indexRequest.source(json, XContentType.JSON);
        if (StringUtils.isNotBlank(requests.getRoutingKey())) {
            indexRequest.routing(requests.getRoutingKey());
        }

        return indexRequest;
    }

    public BulkByScrollResponse updateByQuery(String type, QueryBuilder queryBuilder, String scriptCode, Map<String, Object> scriptParams) {
        Script script = new Script(ScriptType.INLINE, "painless", scriptCode, scriptParams);

        UpdateByQueryRequest updateByQuery = new UpdateByQueryRequest();
        updateByQuery.setDocTypes(type);
        updateByQuery.setQuery(queryBuilder);
        updateByQuery.indices(indexName);
        updateByQuery.setScript(script);

        try {
            BulkByScrollResponse response;
            response = client.updateByQuery(updateByQuery, RequestOptions.DEFAULT);
            client.indices().refresh(new RefreshRequest(indexName), RequestOptions.DEFAULT);
            return response;
        } catch (IOException e) {
            throw new ElasticSearchException("Failed to refresh index " + indexName, e);
        }
    }


    public String update(NgpElasticIndexRequest requests) {
        String json = toJson(requests.getSource());

        UpdateRequest updateRequest = new UpdateRequest(indexName, requests.getType(), requests.getId());
        updateRequest.doc(json, XContentType.JSON);
        updateRequest.setRefreshPolicy(daoRefreshPolicy);

        String result;
        try {
            result = client.update(updateRequest, RequestOptions.DEFAULT).getId();
        } catch (Exception e) {
            throw new ElasticSearchException("Failed to send update request to ElasticSearch. Check inner exception for details", e);
        }


        try {
            client.indices().refresh(new RefreshRequest(indexName), RequestOptions.DEFAULT);
            return result;
        } catch (Exception e) {
            throw new ElasticSearchException("Failed to referesh index " + indexName, e);
        }
    }

/*    public String updateWithScript(String type, String id, String scriptCode, Map<String, Object> scriptParams) {
        return updateWithScript(type, null, id, scriptCode, scriptParams);
    }*/

    public String updateWithScript(String type, String routingKey, String id, String scriptCode, Map<String, Object> scriptParams) {
        UpdateRequest ur = new UpdateRequest(indexName, type, id).routing(routingKey);

        ur.script(new Script(
                ScriptType.INLINE,
                "painless",
                scriptCode,
                scriptParams
        ));
        ur.retryOnConflict(5);

        ur.setRefreshPolicy(daoRefreshPolicy);

        String result;
        try {
            result = client.update(ur, RequestOptions.DEFAULT).getId();
        } catch (Exception e) {
            throw new ElasticSearchException("Failed to send update request to ElasticSearch. Check inner exception for details", e);
        }

        try {
            client.indices().refresh(new RefreshRequest(indexName), RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new ElasticSearchException("Failed to referesh index " + indexName, e);
        }
        return result;
    }



    public SearchResponse aggregatedSearch(String type, AggregationBuilder aggregationBuilder, QueryBuilder queryBuilder) {
        SearchRequest searchRequest = Requests.searchRequest(indexName);
        searchRequest.source().aggregation(aggregationBuilder);
        searchRequest.source().query(queryBuilder);
        searchRequest.source().size(0);
        searchRequest.types(type);
        try {
            return client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticSearchException(FAILED_TO_SEARCH_MESSAGE, e);
        }
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
/*
    public <T extends BaseIndexedDto> List<T> search(String type, QueryBuilder queryBuilder, Class<T> entityClass) {
        return search(type, queryBuilder, entityClass, 1000);
    }

    public <T extends BaseIndexedDto> List<T> search(String type, QueryBuilder queryBuilder, Class<T> entityClass, int resultLimit) {
        return searchFull(type, queryBuilder, entityClass, null, null, resultLimit);
    }

    public <T extends BaseIndexedDto> Page<T> searchPageable(String type, QueryBuilder queryBuilder, Class<T> entityClass, Pageable p) {
        return searchPageable(type, queryBuilder, entityClass, p, null, null, null, false);
    }

    public <T extends BaseIndexedDto> Page<T> searchPageable(String type, QueryBuilder queryBuilder, Class<T> entityClass, Pageable p, Map<String, Object> searchAfter) {
        return searchPageable(type, queryBuilder, entityClass, p, searchAfter, null, null, false);
    }

    public <T extends BaseIndexedDto> Page<T> searchPageable(String type, QueryBuilder queryBuilder, Class<T> entityClass, Pageable p,
                                                             String[] includeFields, String[] excludeFields) {
        return searchPageable(type, queryBuilder, entityClass, p, null, includeFields, excludeFields, false);
    }

    public <T extends BaseIndexedDto> Page<T> searchPageable(String type, QueryBuilder queryBuilder, Class<T> entityClass, Pageable p,
                                                             Map<String, Object> searchAfter, String[] includeFields, String[] excludeFields) {
        return searchPageable(type, queryBuilder, entityClass, p, searchAfter, includeFields, excludeFields, false);
    }*/

    public <T extends BaseIndexedDto> Page<T> searchPageable(String type, QueryBuilder queryBuilder, Class<T> entityClass, Pageable p,
                                                             Map<String, Object> searchAfter, String[] includeFields, String[] excludeFields,
                                                             boolean forceNoDefaultSorting) {
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(type);
        searchRequest.searchType(SearchType.DFS_QUERY_THEN_FETCH);
        searchRequest.source().query(queryBuilder);
        searchRequest.source().size(p.getPageSize());

        if (nonNull(includeFields) || nonNull(excludeFields)) {
            searchRequest.source().fetchSource(includeFields, excludeFields);
        }

        if (nonNull(p.getSort())) {
            p.getSort().forEach(order -> searchRequest.source().sort(orderToSortBuilder(order)));
        }

        if (!forceNoDefaultSorting) {
            //Required to resolve tie cases for searchAfter, then several entities have same value in all sorted columns
            //Should be assigned even if search done without search after parameter - in case of search after first request will be without it.
            searchRequest.source().sort(UID_NAME, SortOrder.ASC);
        }

        if (nonNull(searchAfter) && !searchAfter.isEmpty()) {
            prepareSearchAfter(p, searchRequest, searchAfter);
        } else {
            searchRequest.source().from((int) p.getOffset());
        }

        try {
            SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

            List<T> result = new ArrayList<>();

            int i = 0;
            for (SearchHit searchHitFields : response.getHits()) {
                result.add(JsonUtils.read(searchHitFields.getSourceAsString(), entityClass));
                result.get(i).setUid(type + '#' + searchHitFields.getId());
                result.get(i++).setSortValues(searchHitFields.getSortValues());
            }

            return new PageImpl<>(result, p, response.getHits().getTotalHits());
        } catch (IOException e) {
            throw new ElasticSearchException(FAILED_TO_SEARCH_MESSAGE, e);
        }
    }

    private void prepareSearchAfter(Pageable p, SearchRequest searchRequest, Map<String, Object> searchAfter) {
        //Required by searchAfter logic in elastic
        searchRequest.source().from(0);

        //Retrieve properties which is used in sorts from searchAfter object
        List<Object> searchAfterList = new ArrayList<>();

        //If no sorting applied we still use _uid sorting by default
        if (!Sort.unsorted().equals(p.getSort()) && p.getSort().iterator().hasNext()) {
            p.getSort().forEach(order -> searchAfterList.add(searchAfter.get(order.getProperty())));
        }

        //Required to resolve tie cases, then several entities have same value in all sorted columns.
        //Also used as "default" sort for any queries.
        searchAfterList.add(searchAfter.get(UID_NAME));

        //Search_After works only with String, Text, Long, Integer, Short, Byte, Double, Float, Boolean values
        //For Date values we need to convert them in integer - number of milliseconds from epoch start
        for (int i = 0; i < searchAfterList.size(); i++) {
            Object element = searchAfterList.get(i);
            if (element instanceof ZonedDateTime) {
                searchAfterList.set(i, ((ChronoZonedDateTime<LocalDate>) element).toInstant().toEpochMilli());
            } else if (element instanceof Date) {
                searchAfterList.set(i, ((Date) element).getTime());
            }
        }

        searchRequest.source().searchAfter(searchAfterList.toArray());
    }

    private SortBuilder orderToSortBuilder(Order order) {
        FieldSortBuilder result = SortBuilders.fieldSort(order.getProperty()).order(order.isAscending() ? SortOrder.ASC : SortOrder.DESC);
        // add another cases of nested paths
        if (indexName.equals(cdcbAnimalOrderIndexName)) {
            CdcbAnimalOrderSchema.extractNestedPath(order.getProperty()).ifPresent(path -> result.setNestedSort(new NestedSortBuilder(path)));
        }
        return result;
    }

    public <T extends BaseIndexedDto> T get(NgpElasticGetRequest<T> request) throws IOException {
        GetRequest getRequest = new GetRequest();
        getRequest.id(request.getId());
        getRequest.index(indexName);
        getRequest.type(request.getType());

        GetResponse entity = client.get(getRequest);

        String json = entity.getSourceAsString();
        if (nonNull(json)) {
            logger.debug(json);
            return JsonUtils.read(json, request.getEntityClass());
        } else {
            return null;
        }
    }

    public void remove(String type, String id) {
        DeleteRequest deleteRequest = new DeleteRequest(indexName);
        deleteRequest.type(type);
        deleteRequest.id(id);
        deleteRequest.setRefreshPolicy(daoRefreshPolicy);
        try {
            DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);

            if (response.status() == RestStatus.NOT_FOUND) {
                logger.info("Document id='{}' of type='{}' was not found. Nothing to remove", id, type);
            } else if (response.status() != RestStatus.ACCEPTED && response.status() != RestStatus.OK) {
                throw new ElasticSearchException("Failed to remove document id:" + id);
            }
        } catch (IOException e) {
            throw new ElasticSearchException("Failed to delete", e);
        }
    }

}