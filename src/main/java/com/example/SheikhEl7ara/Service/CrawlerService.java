package com.example.SheikhEl7ara.Service;

import com.example.SheikhEl7ara.Model.Page;
import com.example.SheikhEl7ara.Repository.PageRepository;
import com.google.gson.*;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URI;
import org.json.simple.JSONArray;

import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Optional;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Service
public class CrawlerService {
    private final PageRepository pageRepository;

    @Autowired
    public CrawlerService(PageRepository pageRepository){
        this.pageRepository = pageRepository;
    }

    private static final Object lock = new Object();
    private static final String filePath = "data.json";
    private final ConcurrentHashMap<String, Boolean> visitedUrls = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Boolean> visitedPages = new ConcurrentHashMap<>();
    private final ConcurrentLinkedQueue<String> urlsToVisit = new ConcurrentLinkedQueue<>();
    List<JsonObject> jsonObjectList;

    private int maxNumOfPages;
    private int numOfThreads;
    private List<String> seeds;
    private final AtomicInteger numOfCrawledPages = new AtomicInteger(0);

    private class CrawlerThread implements Runnable{
        @Override
        public void run(){
            System.out.println("thread started");
            while(numOfCrawledPages.get() <= maxNumOfPages) {
                String url = urlsToVisit.poll();
                if (url == null) {
                    System.out.println("the queue is empty exiting");
                    return ;
                }
                String normlizedUrl;
                try {
                    normlizedUrl = normalize(url);
                }
                catch (NullPointerException e){
                    System.err.println("error in  null exception handler"+url);
                    continue;
                }

                if(!Pattern.compile("^(http://|https://).*").matcher(normlizedUrl).matches()){
                    System.out.println("not valid url skip it: "+normlizedUrl);
                    continue;
                }

                if(!isUrlAllowedByRobotsTxt(normlizedUrl)){
                    System.out.println("this url has robot and prevent access"+normlizedUrl);
                    continue;
                }

                try{

                    // if we see this url before
                    if(visitedUrls.putIfAbsent(normlizedUrl,true) != null){
                        System.out.println("found the same url before in this crawling");
                        continue;
                    }

                    Optional<Page> page = pageRepository.findByNormlizedUrl(normlizedUrl);
                    // if we added the page in the DB in previous crawl
                    if(page.isPresent()){
                        page.get().setPopularity(page.get().getPopularity()+1);
                        pageRepository.save(page.get());
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
                    Page newPage = new Page();
                    newPage.setUrl(url);
                    newPage.setNormlizedUrl(normlizedUrl);
                    newPage.setPopularity(1);
                    System.out.println("saving page with normlized url:"+ normlizedUrl);
                    pageRepository.save(newPage);
//                    JsonObject newJsonObject = new JsonObject();
//                    newJsonObject.addProperty("url",normlizedUrl);
//                    newJsonObject.addProperty("html",document.html());
//                    jsonObjectList.add(newJsonObject);

                    System.out.println("done saving page with normlized url:"+ normlizedUrl);
                    visitedUrls.put(normlizedUrl,true);
                    numOfCrawledPages.getAndIncrement();

                    Elements links = document.select("a[href]");
                    for (Element link : links) {
                        String nextUrl = link.absUrl("href");
                        urlsToVisit.offer(nextUrl);
                    }
                }

                catch (IOException e) {
                    System.err.println("error in connection: "+url);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return;
        }
    }

    public void start(List<String> seeds, int numOfThreads, int maxNumOfPages) throws InterruptedException, IOException {
//        FileReader reader = new FileReader("data.json");
//
//        // Parse the JSON array
//        JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
//
//        // Create a list to hold JSON objects
//        jsonObjectList = new ArrayList<>();
//
//        // Add existing objects from JSON array to the list
//        for (JsonElement element : jsonArray) {
//            if (element.isJsonObject()) {
//                jsonObjectList.add(element.getAsJsonObject());
//            }
//        }

        this.maxNumOfPages = maxNumOfPages;
        this.numOfThreads = numOfThreads;
        urlsToVisit.addAll(seeds);
        CountDownLatch latch = new CountDownLatch(numOfThreads); // Create CountDownLatch
        ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
        for (int i = 0; i < numOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    new CrawlerThread().run();
                } finally {
                    latch.countDown(); // Count down when thread finishes
                }
            });
            Thread.sleep(200); // Sleep between thread submissions
        }
        latch.await(); // Wait for all threads to finish
//        JsonArray updatedArray = new JsonArray();
//        for (JsonObject obj : jsonObjectList) {
//            updatedArray.add(obj);
//        }
//
//        // Write the updated JSON array back to the file
//        FileWriter writer = new FileWriter("data.json");
//        writer.write(updatedArray.toString());
//        writer.close();

        System.out.println("File updated successfully.");
    }


    public static String normalize(String urlString) {
        if (urlString.endsWith(".pdf") || urlString.endsWith(".png") || urlString.endsWith(".jpg")) {
            return null;
        }
        urlString = urlString.trim().replaceAll("\\s+", "%20");
        urlString = URLDecoder.decode(urlString, StandardCharsets.UTF_8);
        urlString = urlString.replaceAll("\\/\\/", "/");
        urlString = urlString.replace("https:/", "https://");
        urlString = urlString.replace("http:/", "http://");
        urlString = urlString.replaceAll("\\/+$", "");
        urlString = urlString.replaceAll("\\.\\./+", "/");
        urlString = urlString.toLowerCase();
        urlString = urlString.replaceAll("&+", "&");
        int questionMarkIndex = urlString.indexOf('?');
        if (questionMarkIndex != -1) {
            urlString = urlString.substring(0, questionMarkIndex);
        }
        urlString = urlString.trim();
        try {
            URI uri = new URI(urlString);
            uri = uri.normalize();
            return uri.toString();
        }
        catch (Exception e){
            System.out.println(e);
        }

        return null;
    }


    public static boolean isUrlAllowedByRobotsTxt(String urlToCheck) {
        HashMap<String, Boolean> allowedUrlsCache = new HashMap<>();

        try {
            URL url = new URL(urlToCheck);
            String protocol = url.getProtocol();
            String serverName = url.getHost();
            URL robotsUrl = new URL(protocol + "://" + serverName + "/robots.txt");

            Boolean isAllowed = allowedUrlsCache.get(robotsUrl.toString());
            if (isAllowed != null) {
                return isAllowed;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(robotsUrl.openStream()));
            String line;
            boolean userAgentStatus = false;
            boolean isUrlAllowed = true;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("User-agent:")) {
                    userAgentStatus = line.contains("*") ||  line.contains("SheikhEl7ara");
                } else if (line.startsWith("Disallow:") && userAgentStatus) {
                    if (line.length() >= 11) {
                        String disallowedDirectories = line.substring(10).trim();
                        String disallowedUrl = protocol + "://" + serverName + disallowedDirectories;
                        if (urlToCheck.contains(disallowedUrl)) {
                            isUrlAllowed = false;
                            break;
                        }
                    }
                }
            }

            allowedUrlsCache.put(robotsUrl.toString(), isUrlAllowed);
            return isUrlAllowed;
        } catch (IOException e) {
            e.printStackTrace(); // Handle or log the exception as needed
            return true; // Assume URL is allowed if there's an error reading robots.txt
        }
    }
}