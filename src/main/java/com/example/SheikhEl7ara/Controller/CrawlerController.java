package com.example.SheikhEl7ara.Controller;

import com.example.SheikhEl7ara.Model.Word;
import com.example.SheikhEl7ara.Service.CrawlerService;
import com.example.SheikhEl7ara.Service.WordService;
import org.apache.commons.validator.routines.UrlValidator;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/crawler")
public class CrawlerController {
    private final CrawlerService crawlerService;

    @Autowired
    public CrawlerController(CrawlerService crawlerService){
        this.crawlerService=crawlerService;
    }

    @PostMapping
    public void startCrawling(@RequestBody CrawlerRequest crawlerRequest){
        crawlerService.start(
                crawlerRequest.getSeeds(),
                crawlerRequest.getNumOfThreads(),
                crawlerRequest.getMaxNumOfPages());
    }
}
