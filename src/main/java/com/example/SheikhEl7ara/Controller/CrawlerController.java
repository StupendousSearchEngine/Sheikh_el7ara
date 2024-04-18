package com.example.SheikhEl7ara.Controller;

import com.example.SheikhEl7ara.Service.WordService;
import com.example.SheikhEl7ara.Model.Word;
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
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/crawler")
public class CrawlerController {

    @PostMapping
    public ResponseEntity<String> appendToWordOccurrences(@RequestBody Map<String,String> payload){
//        UrlValidator urlValidator = new UrlValidator();
//        if (urlValidator.isValid(originalUrl)) {
//            String normalizedUrl = originalUrl.split("\\?")[0];
//            assertEquals(expectedNormalizedUrl, manuallyNormalizedUrl);
//        } else {
//            fail(originalUrl);
//        }
        crawl(0,payload.get("url"),new ArrayList<String>());
        return new ResponseEntity<String>("Got it", HttpStatus.CREATED);
    }

    private static void crawl(int level, String url, ArrayList<String> visited){
        if(level <= 5){
            Document doc = request(url,visited);
            DownloadWebPage(doc,visited.size());
            if(doc != null){
                for (Element link:doc.select("a[href]")){
                    String nextLink = link.absUrl("href");
                    if(visited.contains(nextLink) == false){
                        crawl(level+1,nextLink,visited);
                    }
                }
            }
        }
    }

    private static Document request(String url, ArrayList<String> visited) {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();
            if(con.response().statusCode() == 200){
                System.out.println("Link: "+url);
                System.out.println("Title: "+doc.title());
                visited.add(url);
                return doc;
            }
            return null;
        }
        catch (IOException e){
            return null;
        }
    }

    public static void DownloadWebPage(Document document, int index) {
        try {
            FileWriter writer = new FileWriter(new File(String.format("Download%d.html", index)));
            writer.write(document.outerHtml());
            writer.close();
            System.out.println("HTML file saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
