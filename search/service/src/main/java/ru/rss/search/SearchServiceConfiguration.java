package ru.rss.search;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import ru.rss.search.elasticsearch.ElasticSearchConfiguration;

@Configuration
@Import({ElasticSearchConfiguration.class})
@ComponentScan
public class SearchServiceConfiguration {


}
