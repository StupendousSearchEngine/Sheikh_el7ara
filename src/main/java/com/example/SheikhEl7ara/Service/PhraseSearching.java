package com.example.SheikhEl7ara.Service;
import com.example.SheikhEl7ara.Model.Page;
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
    private final PageRepository pageRepository;

    @Autowired
    public PhraseSearching (RankerService rankerService, PageRepository pageRepository,WordRepository wordRepository)
    {
        this.rankerService=rankerService;
        this.pageRepository = pageRepository;

    }
    public HashMap<String, String> logicOperation(String query, int op, String operator) {


        String q1,q2;
        int idx = query.indexOf(operator);
        if (op==0)
        {

            q1=query.substring(0,idx-2);
            q2=query.substring(idx+4);
            System.out.println("Query 1"+q1+" Query 2"+q2);
        }
        else
        {
            q1=query.substring(0,idx-2);
            q2=query.substring(idx+5);
            System.out.println("Query 1"+q1+" Query 2"+q2);

        }
        String[] searchWordsQ1 = q1.split("\\s+");
        HashMap<String, ArrayList<Double>> query1Returns;
        System.out.println(Arrays.toString(searchWordsQ1));
        HashMap<String, String> allURLSQ1 = new HashMap<>();
        query1Returns = this.rankerService.startRanking(searchWordsQ1);

        for (String key : query1Returns.keySet()) {
            System.out.println(key);
            // Convert the body content back to a string if needed
            Page p = pageRepository.findByNormlizedUrl(key);

            String bodyString = "";
            bodyString = p.getHtml();


            // Iterate through <p> elements and extract their text
            Pattern pattern = Pattern.compile(q1);


            Matcher matcher = pattern.matcher(bodyString);

            if (!matcher.find()) {
                bodyString="";

            }


            if (!bodyString.isEmpty())
                allURLSQ1.put(key, bodyString);
        }
        String[] searchWordsQ2 = q2.split("\\s+");

        HashMap<String, ArrayList<Double>> query2Returns;
        System.out.println(Arrays.toString(searchWordsQ2));
        HashMap<String, String> allURLSQ2 = new HashMap<>();

        query2Returns = this.rankerService.startRanking(searchWordsQ2);

        for (String key : query2Returns.keySet()) {
            System.out.println(key);
            // Convert the body content back to a string if needed
            Page p = pageRepository.findByNormlizedUrl(key);

            String bodyString = "";
            bodyString = p.getHtml();


            // Iterate through <p> elements and extract their text
            Pattern pattern = Pattern.compile(q2);


            Matcher matcher = pattern.matcher(bodyString);

            if (!matcher.find()) {
                bodyString = "";

            }


            if (!bodyString.isEmpty())
                allURLSQ2.put(key, bodyString);
        }

        HashMap<String, String> allURLSQTotal = new HashMap<>();

        for (Map.Entry<String, String> entry : allURLSQ1.entrySet()) {
            allURLSQTotal.putIfAbsent(entry.getKey(),entry.getValue());
        }
        if (op==0) {
            System.out.println("OR");
            for (Map.Entry<String, String> entry : allURLSQ2.entrySet()) {
                allURLSQTotal.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
        if(op==1) {
            System.out.println("AND");
            Pattern pattern = Pattern.compile(q2);




            for (Map.Entry<String, String> entry : allURLSQTotal.entrySet()){
                Matcher matcher = pattern.matcher(entry.getValue());
                if (allURLSQ2.containsKey(entry.getKey())||matcher.find()) {
                } else {

                    allURLSQTotal.remove(entry.getKey());
                }

            }
        }
        if (op==2) {
            System.out.println("NOT");

            for (Map.Entry<String, String> entry : allURLSQ2.entrySet()) {

                if (allURLSQTotal.containsKey(entry.getKey()))
                    allURLSQTotal.remove(entry);

            }
            for (Map.Entry<String, String> entry : allURLSQTotal.entrySet()) {
                Pattern pattern = Pattern.compile(q2);
                System.out.println(entry.getValue());
                Matcher matcher = pattern.matcher(entry.getValue());
                if (matcher.find()) {
                    System.out.println("FOUNDDDDDDDDDDDDDDDDD");
                    allURLSQTotal.remove(entry.getKey());
                }

            }

        }
        return allURLSQTotal;




    }
    public HashMap<String, String> queryParser(String query) {

        HashMap<String, ArrayList<Double>> queryReturns;
        String[] searchWords = query.split("\\s+");
        List<String> searchList = Arrays.asList(searchWords);
        if(searchList.contains("OR"))
            return logicOperation(query,0,"OR");
        else if (searchList.contains("AND"))
            return logicOperation(query,1,"AND");
        else if (searchList.contains("NOT"))
            return logicOperation(query,2,"NOT");



        System.out.println("IN BIG FUNC"+Arrays.toString(searchWords));
        HashMap<String, String> allURLS = new HashMap<>();
        queryReturns = this.rankerService.startRanking(searchWords);



        for (String key : queryReturns.keySet()) {
            System.out.println(key);
            // Convert the body content back to a string if needed
            Page p = pageRepository.findByNormlizedUrl(key);

            String bodyString = "";
            bodyString = p.getHtml();


            // Iterate through <p> elements and extract their text
            Pattern pattern = Pattern.compile(query);


            Matcher matcher = pattern.matcher(bodyString);

            if (!matcher.find()) {
                bodyString="";

            }


            if (!bodyString.isEmpty())
                allURLS.put(key, bodyString);

        }

        return allURLS;
    }
}