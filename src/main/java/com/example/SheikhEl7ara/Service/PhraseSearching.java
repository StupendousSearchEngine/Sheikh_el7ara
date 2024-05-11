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
    public HashMap<String, String> queryParser(String query) {
        System.out.println(query);
        query = query.substring(1,query.length()-1);
        System.out.println("search word "+query);
        HashMap<String, String> resultList = new HashMap<>();
        StringTokenizer tokenizer = new StringTokenizer(query);
        int countToken=0;
        //make a map of all urls vaild
        HashMap<String, ArrayList<Double>> tokenResult;
        HashMap<String, Set<Double>> allURLS= new HashMap<>();
        while (tokenizer.hasMoreTokens()) {
            ++countToken;
            String token = tokenizer.nextToken();
            tokenResult = rankerService.startRanking(token);


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
        for (String key: allURLS.keySet()) {
            resultList.put(key, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla pretium urna vel sapien blandit, quis eleifend velit tristique. Aliquam erat volutpat. Nullam dignissim ligula a libero dictum, id tristique mi venenatis. Duis ut nunc metus. Vestibulum tempor, elit et luctus commodo, nunc libero vestibulum justo, non interdum tortor est vel risus. Fusce porttitor eros nec erat malesuada bibendum. Nunc ut nisl id turpis accumsan mattis. Nam vel metus id ipsum elementum eleifend. Nulla suscipit massa nec justo condimentum, quis tincidunt mi fermentum. Nam eget diam nec magna congue facilisis. Maecenas consectetur risus id metus fringilla eleifend. Sed at elit nec libero sodales pharetra. Suspendisse commodo tempor fringilla. Phasellus faucibus, lorem a vestibulum hendrerit, libero est dictum enim, nec viverra justo nisl nec mi.\n" +
                    "\n" +
                    "Maecenas quis nunc vitae nunc sodales tincidunt a quis sapien. Curabitur accumsan risus eget elit faucibus, ac fermentum turpis lacinia. Cras quis risus vitae odio consequat dapibus. Morbi in elit ipsum. Suspendisse potenti. Pellentesque nec lectus ultricies, lobortis ligula nec, bibendum metus. Ut vitae vehicula enim, nec dictum est. Nulla facilisi. Ut id odio et nulla ullamcorper vehicula. Integer placerat elit id ipsum varius laoreet. Vivamus eu pharetra elit. Donec tempus orci et nunc viverra, vel cursus ipsum vestibulum. Integer varius, lorem ut tincidunt fermentum, massa arcu pharetra nunc, non faucibus quam tellus et felis.\n" +
                    "\n" +
                    "Suspendisse potenti. Aliquam aliquet feugiat mauris vitae vestibulum. Morbi ut purus a sapien vehicula faucibus. Phasellus mattis metus vel tortor hendrerit, id consequat nulla varius. Aenean vel bibendum sapien, a ultrices elit. Integer a accumsan nunc. Fusce sed consectetur velit, nec congue dui. Maecenas fermentum scelerisque nulla, non venenatis sapien finibus in. Curabitur euismod, elit sit amet maximus pharetra, sapien turpis rhoncus odio, a lacinia mi urna et nulla. Vivamus suscipit libero vel convallis vestibulum. Curabitur vitae magna ac erat dictum consectetur.\n");
        }
        return resultList;
    }
}
