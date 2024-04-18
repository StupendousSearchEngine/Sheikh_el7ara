package com.example.SheikhEl7ara.Service;
import com.example.SheikhEl7ara.Repository.PageRepository;
import com.example.SheikhEl7ara.Repository.WordRepository;
//import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class PhraseSearching {
    private final RankerService rankerService;
    @Autowired
    public PhraseSearching (RankerService rankerService, PageRepository pageRepository,WordRepository wordRepository)
    {
        this.rankerService=rankerService;
    }
    public HashMap<String, ArrayList<Double>> queryParser(String query) {
        HashMap<String, ArrayList<Double>> resultList = new HashMap<>();
        StringTokenizer tokenizer = new StringTokenizer(query);
        int countToken=0;
        //make a map of all urls vaild

        HashMap<String, Set<Double>> allURLS= new HashMap<>();
        while (tokenizer.hasMoreTokens()) {
            ++countToken;
            String token = tokenizer.nextToken();
            HashMap<String, ArrayList<Double>> tokenResult = rankerService.startRanking(token);
            resultList=tokenResult;

            /*
            resultsList
            Example for word 'semaphor'
            {"https://www.javatpoint.com/python-tutorial":[1.2805941802660205E-4,1946.0],
            "https://www.javatpoint.com/python-tkinter":[7.485356323803721E-5,1943.0],
            "https://www.javatpoint.com/operating-system":[2.1402852306856167E-4,798.0,801.0,805.0,807.0,1212.0,1282.0,1487.0,2791.0,2794.0,2798.0,2800.0]}

            URL:Array

            first element in array is tf_idf value
            the rest of the elemens are the position of the token in the url

            ignore the first element in the array if you care about positions only
             */
            /*
                start to do:
                    - make a map of all urls (/)
                    - if 1st token
                        -add all urls to the map
                        -add their respected positions in a set
                            -map<url, set<positions>> of first element
                        - keep count of which token
                        - subtract position from first element's +1 (1 indexed)
             */
            if (countToken == 1)
            {
                for(String url : tokenResult.keySet()) {
                    ArrayList<Double> firstPositions=tokenResult.get(url);
                    Set<Double> dummyInsert = new HashSet<>(firstPositions);
                    allURLS.put(url, dummyInsert);

                }

            }
            else
            {
                for(String url :allURLS.keySet())
                {
                    if (!tokenResult.containsKey(url)) {
                        allURLS.remove(url);
                        continue;
                    }
                    for (int i=0;   i<tokenResult.get(url).size();  i++)
                    {
                        Double pos = tokenResult.get(url).get(i);
                        if (!allURLS.get(url).contains(pos-countToken+1))
                            allURLS.remove(url);
                    }


                }




            }

            //add function to call ranker and get result
            //add result of ranker to a map
            //if not first token
            //if map ;has result doc :
            //find if position of token is equal position of prev token +1
            //if yes
            //continue
            //if no
            //remove result from map


        }
        return resultList;
    }
}
