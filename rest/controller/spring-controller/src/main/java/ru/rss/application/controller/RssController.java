package ru.rss.application.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class RssController {
    @GetMapping("/list")
    public String getRssList(){
        return "fsvfsdf";
    }
}
