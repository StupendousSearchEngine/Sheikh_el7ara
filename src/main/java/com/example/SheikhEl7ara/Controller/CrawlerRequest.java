package com.example.SheikhEl7ara.Controller;

import java.util.List;

public class CrawlerRequest {
    private List<String> seeds;
    private int numOfThreads;
    private int maxNumOfPages;

    public CrawlerRequest(){
    }

    public List<String> getSeeds() {
        return seeds;
    }

    public void setSeeds(List<String> seeds) {
        this.seeds = seeds;
    }

    public int getNumOfThreads() {
        return numOfThreads;
    }

    public void setNumOfThreads(int numOfThreads) {
        this.numOfThreads = numOfThreads;
    }

    public int getMaxNumOfPages() {
        return maxNumOfPages;
    }

    public void setMaxNumOfPages(int maxNumOfPages) {
        this.maxNumOfPages = maxNumOfPages;
    }
}
