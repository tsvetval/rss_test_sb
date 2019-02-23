package ru.rss.search.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.util.Arrays;

public class ElasticSearchConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ElasticSearchConfiguration.class);
    public static final String RSS_FEED_DAO = "rssFeedDao";

    @Value("${es.hosts}")
    private String[] hosts;

    @Value("${es.port:9200}")
    private int port;

    @Value("${es.scheme:http}")
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
    public ElasticSearchDao getRssFeedElasticSearchDao(
            @Value("${es.refresh.policy.immediate.refresh:true}") String refreshPolicy,
            RestHighLevelClient client) {

        ElasticSearchDao elasticSearchDao = new ElasticSearchDao("rss_feed", refreshPolicy, client);
        elasticSearchDao.initIndexFromFile("ru/rss/config/elasticsearch/rss-feed-index-config.json");
        return elasticSearchDao;
    }
}