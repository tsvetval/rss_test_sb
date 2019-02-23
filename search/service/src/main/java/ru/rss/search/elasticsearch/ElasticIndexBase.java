package ru.rss.search.elasticsearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

abstract class ElasticIndexBase {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    final RestHighLevelClient client;
    final ObjectMapper elasticObjectMapper;
    final String indexName;


    ElasticIndexBase(RestHighLevelClient client, String indexName) {
        this.client = client;
        this.elasticObjectMapper = new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS)
                .enable(JsonParser.Feature.ALLOW_COMMENTS)
                .setSerializationInclusion(JsonInclude.Include.ALWAYS)
                .configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false);
        this.indexName = indexName;
    }

    boolean initIndexFromFile(String fileRelativePath) {
        logger.debug("try to init index '{}' by file={}", indexName, fileRelativePath);

        String sourceStr = null;
        try {
            logger.debug("Reading file {}", fileRelativePath);
            InputStream inputStream = new ClassPathResource(fileRelativePath).getInputStream();
            sourceStr = IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException e) {
            throw new ElasticIndexInitializeException("Failed to read file for index creation", e);
        }

        return initIndex(sourceStr);
    }

    @SuppressWarnings("This is exampel, so some field are redundant and silly")
    boolean initIndex(String source) {
        logger.debug("try to init index '{}' by source={}", indexName, source);

        if (isNull(client) || isNull(indexName)) {
            throw new IllegalStateException("object not inited");
        }

        boolean recreate = true; // TODO we check this via version
        boolean isExist = isExists();

        if (isExist && recreate) {
            DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest(indexName);
            try {
                client.indices().delete(deleteIndexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new ElasticSearchException("Failed to delete index " + indexName, e);
            }
            isExist = false;
        }

        if (!isExist) {
            CreateIndexRequest createIndexRequest = new CreateIndexRequest(indexName);
            if (nonNull(source)) {
                createIndexRequest.source(source, XContentType.JSON);
            }
            try {
                client.indices().create(createIndexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new ElasticSearchException("Failed to create index " + indexName, e);
            }

            return true;
        }
        return false;
    }


    private boolean isExists() {
        GetIndexRequest getIndexRequest = new GetIndexRequest();
        getIndexRequest.indices(indexName);
        try {
            return client.indices().exists(getIndexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new ElasticSearchException("Failed to check whether index " + indexName + " exists", e);
        }
    }


    String toJson(Object o) {
        String json;
        try {
            json = elasticObjectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new ElasticSearchException("Cant serialize object of type", e);
        }
        return json;
    }
}
