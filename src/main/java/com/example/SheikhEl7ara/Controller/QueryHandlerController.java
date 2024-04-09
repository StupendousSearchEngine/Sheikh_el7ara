package com.example.SheikhEl7ara.Controller;
import edu.stanford.nlp.io.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.*;
import com.example.SheikhEl7ara.Model.Word;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.ie.util.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.trees.*;
import java.util.*;

import org.springframework.http.HttpStatus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import java.util.Properties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import java.util.ArrayList;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

@RestController
public class QueryHandlerController {


    public static String findRoot(String searchWord) {
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);




        Annotation document = new Annotation(searchWord);
        pipeline.annotate(document);
        if (searchWord.endsWith("er") && searchWord.length() > 2) {
            searchWord = searchWord.substring(0, searchWord.length()-2);

        }


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



    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @PostMapping("/api/search")

    public ResponseEntity<String> handleFormData(@RequestParam("textData") String textData)
    {
        try {

            System.out.println("Received text data: " + textData);


            Gson gson = new Gson();



            System.out.println("Received text data: "+textData);
            String stemmedSearchPhrase=findRoot(textData);
            System.out.println("Search phrase after stripping "+stemmedSearchPhrase);
            String jsonResponse = gson.toJson("Data received and processed successfully. ");

            // Return a success response with JSON content type
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonResponse);
        } catch (Exception e) {
            // If an error occurs, return an error response
            Gson gson = new Gson();
            String errorResponse = gson.toJson("An error occurred while processing the data.");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorResponse);
        }

    }



}
