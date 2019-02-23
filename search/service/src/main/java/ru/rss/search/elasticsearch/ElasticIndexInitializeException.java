package ru.rss.search.elasticsearch;

public class ElasticIndexInitializeException extends RuntimeException{
    public ElasticIndexInitializeException(String message) {
        super(message);
    }

    public ElasticIndexInitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
