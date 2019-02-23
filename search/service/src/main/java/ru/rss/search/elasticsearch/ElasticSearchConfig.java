package ru.rss.search.elasticsearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Arrays;

public class ElasticSearchConfig {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchConfig.class);
    public static final String RSS_FEED_DAO = "rssFeedDao";
    public static final String ELASTIC_OBJECT_MAPPER = "elasticObjectMapper";

    @Value("${es.hosts}")
    private String[] hosts;

    @Value("${es.port:443}")
    private int port;

    @Value("${es.scheme:https}")
    private String scheme;

    @Bean
    public RestHighLevelClient transportClient() {
        RestHighLevelClient client = null;
        boolean esConnected = false;
        for (String host : hosts) {
            client = new RestHighLevelClient(RestClient.builder(new HttpHost(host, port, scheme)));
            log.info("Connecting to ES instance [{}://{}:{}]", scheme, host, port);
            try {
                esConnected = client.ping(RequestOptions.DEFAULT);
                log.info("ES instance [{}://{}:{}] accessible", scheme, host, port);
            } catch (IOException e) {
                String errorMessage = String.format("Failed to send a ping request to ES instance. [host = %s, port = %s, scheme = %s]", host, port, scheme);
                throw new ElasticSearchException(errorMessage, e);
            }
        }

        if (!esConnected) {
            throw new ElasticSearchException("There no accessible ES hosts. hosts=" + Arrays.toString(hosts));
        }

        return client;
    }


    @Bean(RSS_FEED_DAO)
    public ElasticSearchDao getAggregatedAnimalElasticSearchDao(
            @Value("${es.aggregated-animal.index.name}") String indexName,
            @Value("${es.refresh.policy.immediate.refresh:true}") String refreshPolicy,
            @Value("${es.scroll.keeep-alive:60000}") int keepAliveInMs,
            RestHighLevelClient client) {

        ElasticSearchDao animalElasticDao = new ElasticSearchDao(indexName, refreshPolicy, keepAliveInMs, client);
        animalElasticDao.initIndexFromFile("ru/rss/config/elasticsearch/rss-feed-index-config.json");
        return animalElasticDao;
    }

    @Bean
    @Qualifier(ELASTIC_OBJECT_MAPPER)
    public ObjectMapper objectMapper() {
        return   new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS)
                .enable(JsonParser.Feature.ALLOW_COMMENTS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}