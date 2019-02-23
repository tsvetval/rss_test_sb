package ru.rss.search.elasticsearch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.collect.ImmutableOpenMap;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public abstract class ElasticSearchBaseDao {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    final RestHighLevelClient client;
    final ObjectMapper elasticObjectMapper;
    final String indexName;


    public ElasticSearchBaseDao(RestHighLevelClient client, ObjectMapper elasticObjectMapper, String indexName) {
        this.client = client;
        this.elasticObjectMapper = elasticObjectMapper;
        this.indexName = indexName;
    }

    public boolean initIndexFromFile(String fileRelativePath) {
        logger.debug("try to init index '{}' by file={}", indexName, fileRelativePath);

        String sourceStr = null;
        try {
            logger.debug("Reading file {}", fileRelativePath);
            InputStream inputStream = new ClassPathResource(fileRelativePath).getInputStream();
            sourceStr = IOUtils.toString(inputStream, "UTF-8");
        } catch (IOException e) {
            throw new ESIndexInitializeException("Failed to read file for index creation", e);
        }

        return initIndex(sourceStr);
    }

    public boolean initIndex(String source) {
        logger.debug("try to init index '{}' by source={}", indexName, source);

        if (isNull(client) || isNull(indexName)) {
            throw new IllegalStateException("object not inited");
        }

        //index file mappings
        Map<String, String> indexFileVersions = getIndexFileMappingVersions(source);

        //check existing mappings
        boolean recreate = checkNeedRecreate(indexFileVersions);

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

    private boolean checkNeedRecreate(Map<String, String> indexFileVersions) {
        for (Map.Entry<String, String> entry : indexFileVersions.entrySet()) {
            String mapping = entry.getKey();
            if (!isExists()) {
                return false;
            }
            try {
                GetMappingsRequest getMappingsRequest = new GetMappingsRequest();
                getMappingsRequest.indices(indexName);
                getMappingsRequest.masterNodeTimeout((String) null);

                GetMappingsResponse response = client.indices().getMapping(getMappingsRequest, RequestOptions.DEFAULT);
                ImmutableOpenMap<String, ImmutableOpenMap<String, MappingMetaData>> mappings = response.mappings();
                ImmutableOpenMap<String, MappingMetaData> mappingMetaDatas = mappings.get(indexName);
                MappingMetaData mappingMetaData = mappingMetaDatas.get(mapping);
                // new mapping added to an old index - recreate
                if (isNull(mappingMetaData)) {
                    return true;
                }

                String metaContent = mappingMetaData.source().string();
                JsonNode meta = elasticObjectMapper.reader().readTree(metaContent);

                return !Objects.equals(
                        meta.path("_meta").path("version").textValue(),
                        indexFileVersions.get(mapping));
            } catch (IOException e) {
                throw new ESIndexInitializeException("Error validating new index data against existing indices", e);
            }
        }
        return false;
    }

    private Map<String, String> getIndexFileMappingVersions(String source) {
        Map<String, String> result = new HashMap<>();

        if (source == null) {
            return result;
        }

        try {
            JsonNode index = elasticObjectMapper.readTree(source).get("mappings");
            index.fields().forEachRemaining(field -> {
                String mapping = field.getKey();
                String version = field.getValue().path("_meta").path("version").textValue();
                result.put(mapping, version);
            });

            return result;
        } catch (Exception e) {
            logger.error("source file: {}", source);
            throw new ESIndexInitializeException("Looks like your index file has no version set or any other IO error occured.", e);
        }
    }


    public String toJson(Object o) {
        String json;
        try {
            json = elasticObjectMapper.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cant serialize object of type", e);
        }
        return json;
    }
}
