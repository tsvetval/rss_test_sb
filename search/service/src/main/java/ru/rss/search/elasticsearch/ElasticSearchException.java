package ru.rss.search.elasticsearch;

public class ElasticSearchException extends RuntimeException {
    public ElasticSearchException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }


    public ElasticSearchException(String errorMessage) {
        super(errorMessage);
    }
}
