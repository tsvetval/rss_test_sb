package ru.rss.search.elasticsearch;

public class ESIndexInitializeException extends RuntimeException{
    public ESIndexInitializeException(String message) {
        super(message);
    }

    public ESIndexInitializeException(String message, Throwable cause) {
        super(message, cause);
    }
}
