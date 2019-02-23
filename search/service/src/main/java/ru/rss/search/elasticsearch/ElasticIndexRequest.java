package ru.rss.search.elasticsearch;

public class ElasticIndexRequest {
    private String id;
    private String type;
    private Object source;
    private String routingKey;

    public ElasticIndexRequest(String id, String type, Object source) {
        this.id = id;
        this.type = type;
        this.source = source;
    }

    public ElasticIndexRequest(String id, String type, Object source, String routingKey) {
        this.id = id;
        this.type = type;
        this.source = source;
        this.routingKey = routingKey;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Object getSource() {
        return source;
    }

    public String getRoutingKey() {
        return routingKey;
    }
}
