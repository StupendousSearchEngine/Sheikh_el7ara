package com.example.SheikhEl7ara.Controller;

import com.example.SheikhEl7ara.Service.IndexerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/index"})
public class IndexerController {
    private final IndexerService indexerService;

    @Autowired
    public IndexerController(IndexerService indexerService) {
        this.indexerService = indexerService;
    }

    @PostMapping
    public void startIndexing(@RequestBody IndexerRequest indexerRequest) {
        System.out.println("Indexing.....");
        int numThreads = indexerRequest.getNumThreads();
        indexerService.setIndexerThreads(numThreads);
        indexerService.startIndexing();
    }
}
