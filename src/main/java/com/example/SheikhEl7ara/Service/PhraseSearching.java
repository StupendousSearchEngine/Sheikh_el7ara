package com.example.SheikhEl7ara.Service;

import org.jsoup.select.QueryParser;
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
public class PhraseSearching {
    public static String queryParser(String query)
    {
        StringTokenizer tokenizer = new StringTokenizer(query);
        while (tokenizer.hasMoreTokens())
        {
            String token=tokenizer.nextToken();
            //add function to call ranker and get result
            //add result of ranker to a map
            //if not first token
                //if map has result doc :
                    //find if position of token is equal position of prev token +1
                    //if yes
                        //continue
                    //if no
                        //remove result from map
        }


        //return dummy
        return "";
    }
}
