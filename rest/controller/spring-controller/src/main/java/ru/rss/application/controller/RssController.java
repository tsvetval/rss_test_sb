package ru.rss.application.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.rss.aggregator.entity.RssFeed;
import ru.rss.aggregator.port.AggregatorService;

import java.util.List;

@RestController
@RequestMapping
public class RssController {
    @Autowired
    private AggregatorService aggregatorService;

    @GetMapping("/list")
    public List<RssFeed> getRssList(){
        return aggregatorService.readAll();
    }
}
