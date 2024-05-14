package com.example.SheikhEl7ara.Service;
import com.example.SheikhEl7ara.Model.Page;
import edu.stanford.nlp.ling.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import com.example.SheikhEl7ara.Repository.PageRepository;
import com.example.SheikhEl7ara.Repository.WordRepository;
//import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;
import com.example.SheikhEl7ara.Repository.PageRepository;
import com.example.SheikhEl7ara.Repository.WordRepository;

@Service
public class QueryHandler {
    private final RankerService rankerService;
    private final PageRepository pageRepository;

    @Autowired
    public QueryHandler (RankerService rankerService, PageRepository pageRepository,WordRepository wordRepository)
    {
        this.rankerService=rankerService;
        this.pageRepository=pageRepository;

    }
    public static String findRoot(String searchWord) {
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

        Annotation document = new Annotation(searchWord);
        pipeline.annotate(document);

        StringBuilder lemma= new StringBuilder();
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sent : sentences) {
            for (CoreLabel token : sent.get(CoreAnnotations.TokensAnnotation.class)) {
                // Get lemma
                lemma.append(token.get(CoreAnnotations.LemmaAnnotation.class));

            }
        }
        // Shutdown Stanford CoreNLP pipeline
        pipeline.unmount();
        return lemma.toString();

    }
    public static int countLines(StringBuilder sb) {
        int count = 0;
        int length = sb.length();
        for (int i = 0; i < length; i++) {
            if (sb.charAt(i) == '\n') {
                count++;
            }
        }
        // Add 1 to count the last line if it doesn't end with a newline character
        if (length > 0 && sb.charAt(length - 1) != '\n') {
            count++;
        }
        return count;
    }
    public HashMap<String, String> queryReturn(String searchWord)
    {

        HashMap<String, ArrayList<Double>> queryReturns;
        String[] searchWords = searchWord.split("\\s+");

        System.out.println(Arrays.toString(searchWords));
        for (int i = 0; i < searchWords.length; i++)
        {
            searchWords[i]=findRoot(searchWords[i]);
        }
        queryReturns=this.rankerService.startRanking(searchWords);
        System.out.println("debuuuuuug");
        System.out.println(queryReturns.toString());
        HashMap<String, String> allURLS= new HashMap<>();
        for (String key : queryReturns.keySet()) {
//            System.out.println(key);


            // Convert the body content back to a string if needed
            Page p = pageRepository.findByNormlizedUrl(key);

            String bodyString = "";
            bodyString = p.getHtml();






            allURLS.put(key, bodyString);


        }



        return (allURLS);

    }
}
