package com.example.SheikhEl7ara.Service;

import com.example.SheikhEl7ara.Model.Page;
import com.example.SheikhEl7ara.Repository.PageRepository;
import lombok.Setter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Optional;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Service
public class CrawlerService {
    private final PageRepository pageRepository;

    @Autowired
    public CrawlerService(PageRepository pageRepository){
        this.pageRepository = pageRepository;
    }

    private final ConcurrentHashMap<String, Boolean> visitedUrls = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> visitedPages = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<String> urlsToVisit = new ConcurrentLinkedQueue<>();
    private int maxNumOfPages;
    private int numOfThreads;
    private List<String> seeds;
    private final AtomicInteger numOfCrawledPages = new AtomicInteger(0);

    private class CrawlerThread implements Runnable{
        @Override
        public void run(){
            while(numOfCrawledPages.get() <= maxNumOfPages) {
                String url = urlsToVisit.poll();
                if (url == null) {
                    System.out.println("out from line number 51");
                    return;
                }

                if(!Pattern.compile("^(http://|https://).*").matcher(url).matches()){
                    System.out.println("not valid url line number 56");
                    continue;
                }

                try{
                    String normlizedUrl = normalize(url);
                    // if we see this url before
                    if(visitedUrls.putIfAbsent(normlizedUrl,true) != null){
                        System.out.println("found url before line number 64");
                        continue;
                    }
                    // if we added the page in the DB in previous crawl
                    if(pageRepository.findByNormlizedUrl(normlizedUrl).isPresent()){
                        System.out.println("found page in DB line number 69");
                        continue;
                    }

                    Connection connection = Jsoup.connect(url);
                    Document document = connection.get();
                    if(connection.response().statusCode() == 200){
                        String contentType = connection.response().contentType();
                        if (contentType == null || !contentType.startsWith("text/html")) {
                            System.out.println("not an html page line number 78");
                            continue;
                        }
                    }

                    Page page = new Page();
                    page.setUrl(url);
                    page.setNormlizedUrl(normlizedUrl);
                    System.out.println("saving page with normlized url:"+ normlizedUrl);
                    pageRepository.save(page);
                    System.out.println("done saving page with normlized url:"+ normlizedUrl);
                    visitedUrls.put(normlizedUrl,true);
                    numOfCrawledPages.getAndIncrement();

                    Elements links = document.select("a[href]");
                    for (Element link : links) {
                        String nextUrl = link.absUrl("href");
                        urlsToVisit.offer(nextUrl);
                    }
                }
                catch (NullPointerException e){
                    System.err.println("error in putIfAbsent"+url);
                }
                catch (IOException e) {
                    System.err.println("error in connection: "+url);
                }
            }
        }
    }

    public void start(List<String> seeds, int numOfThreads, int maxNumOfPages){
        this.maxNumOfPages = maxNumOfPages;
        this.numOfThreads = numOfThreads;
        urlsToVisit.addAll(seeds);

        ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
        for (int i = 0; i < numOfThreads; i++) {
            executorService.submit(new CrawlerThread());
        }

        executorService.shutdown();
    }

    public static String normalize(String urlString) {
        try {
            urlString = urlString.trim().replaceAll("\\s+", " ");

            urlString = URLDecoder.decode(urlString, "UTF-8");

            URI uri = new URI(urlString);

            uri = uri.normalize();

            return uri.toString();
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

}
