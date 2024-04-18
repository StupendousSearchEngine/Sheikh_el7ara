package com.example.SheikhEl7ara.Controller;
import com.example.SheikhEl7ara.Service.PhraseSearching;
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
import  com.example.SheikhEl7ara.Service.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/query")
public class QueryHandlerController {
    private static QueryHandler queryHandler;


    @Autowired
    public QueryHandlerController(QueryHandler queryHandler) {
        this.queryHandler = queryHandler;
    }

    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @PostMapping("/search")
    public ResponseEntity<String> handleFormData(@RequestParam("textData") String textData) {
        try {

            System.out.println("Received text data: " + textData);


            Gson gson = new Gson();


            //QueryHandler queryHandler= new QueryHandler();
            System.out.println("Received text data: " + textData);
            //String stemmedSearchPhrase= QueryHandler.findRoot(textData);


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

    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @GetMapping("/{word}")
    public static ResponseEntity<Optional<HashMap<String, String>>> respondQuery(@PathVariable String word) {
        System.out.println("data in API");
        return new ResponseEntity<Optional<HashMap<String, String>>>
                (Optional.ofNullable(queryHandler.queryReturn(word)), HttpStatus.OK);

    }


}
