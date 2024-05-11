package com.example.SheikhEl7ara.Service;
import com.example.SheikhEl7ara.Repository.PageRepository;
import com.example.SheikhEl7ara.Repository.WordRepository;
//import javafx.util.Pair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PhraseSearching {
    private final RankerService rankerService;
    @Autowired
    public PhraseSearching (RankerService rankerService, PageRepository pageRepository,WordRepository wordRepository)
    {
        this.rankerService=rankerService;
    }
    public HashMap<String, String> queryParser(String query) {

        HashMap<String, ArrayList<Double>> queryReturns;
        String[] searchWords = query.split("\\s+");
        System.out.println(Arrays.toString(searchWords));
        HashMap<String, String> allURLS = new HashMap<>();
        for (int i = 0; i < searchWords.length; i++) {
            //add this for now and try to modify it later as a list
            /*if (searchWords[i].matches(".*\\d+.*"))
                queryReturns=this.rankerService.startRanking(searchWords[i]);
            else*/
            queryReturns = this.rankerService.startRanking(searchWords[i]);

            for (String key : queryReturns.keySet()) {
                System.out.println(key);


                Connection connect = Jsoup.connect(key);

                Document document = null;
                try {
                    document = connect.get();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                String html = document.html();
                Document doc = Jsoup.parse(html);

                // Extract the body content
                Element body = doc.body();

                // Convert the body content back to a string if needed
                Elements paragraphs = body.select("p");

                StringBuilder bodyString = new StringBuilder();
                // Iterate through <p> elements and extract their text
                Pattern pattern = Pattern.compile(query);

                for (Element paragraph : paragraphs) {
                    String paragraphText = paragraph.text();
                    Matcher matcher = pattern.matcher(paragraphText);

                    if (matcher.find()) {
                        bodyString.append(paragraphText).append("\n");

                    }

                }
                if (!bodyString.isEmpty())
                    allURLS.put(key, bodyString.toString());

            }
        }
        return allURLS;
    }
}