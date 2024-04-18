package com.example.SheikhEl7ara.Service;
import edu.stanford.nlp.ling.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Pair;
import org.springframework.stereotype.Service;

import com.example.SheikhEl7ara.Repository.PageRepository;
import com.example.SheikhEl7ara.Repository.WordRepository;
//import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;
@Service
public class QueryHandler {
    private final RankerService rankerService;
    @Autowired
    public QueryHandler (RankerService rankerService, PageRepository pageRepository,WordRepository wordRepository)
    {
        this.rankerService=rankerService;
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
    public HashMap<String, String> queryReturn(String searchWord)
    {
        HashMap<String, ArrayList<Double>> queryReturns = new HashMap<>();
        String[] searchWords = searchWord.split("\\s+");
        System.out.println(Arrays.toString(searchWords));
        HashMap<String, String> allURLS= new HashMap<>();
        for (int i = 0; i < searchWords.length; i++)
        {
            //add this for now and try to modify it later as a list
            if (searchWords[i].matches(".*\\d+.*"))
                queryReturns=this.rankerService.startRanking(searchWords[i]);
            else
                queryReturns=this.rankerService.startRanking(findRoot(searchWords[i]));
            if (i==0) {
                for (String key : queryReturns.keySet())
                    allURLS.put(key, "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nulla pretium urna vel sapien blandit, quis eleifend velit tristique. Aliquam erat volutpat. Nullam dignissim ligula a libero dictum, id tristique mi venenatis. Duis ut nunc metus. Vestibulum tempor, elit et luctus commodo, nunc libero vestibulum justo, non interdum tortor est vel risus. Fusce porttitor eros nec erat malesuada bibendum. Nunc ut nisl id turpis accumsan mattis. Nam vel metus id ipsum elementum eleifend. Nulla suscipit massa nec justo condimentum, quis tincidunt mi fermentum. Nam eget diam nec magna congue facilisis. Maecenas consectetur risus id metus fringilla eleifend. Sed at elit nec libero sodales pharetra. Suspendisse commodo tempor fringilla. Phasellus faucibus, lorem a vestibulum hendrerit, libero est dictum enim, nec viverra justo nisl nec mi.\n" +
                            "\n" +
                            "Maecenas quis nunc vitae nunc sodales tincidunt a quis sapien. Curabitur accumsan risus eget elit faucibus, ac fermentum turpis lacinia. Cras quis risus vitae odio consequat dapibus. Morbi in elit ipsum. Suspendisse potenti. Pellentesque nec lectus ultricies, lobortis ligula nec, bibendum metus. Ut vitae vehicula enim, nec dictum est. Nulla facilisi. Ut id odio et nulla ullamcorper vehicula. Integer placerat elit id ipsum varius laoreet. Vivamus eu pharetra elit. Donec tempus orci et nunc viverra, vel cursus ipsum vestibulum. Integer varius, lorem ut tincidunt fermentum, massa arcu pharetra nunc, non faucibus quam tellus et felis.\n" +
                            "\n" +
                            "Suspendisse potenti. Aliquam aliquet feugiat mauris vitae vestibulum. Morbi ut purus a sapien vehicula faucibus. Phasellus mattis metus vel tortor hendrerit, id consequat nulla varius. Aenean vel bibendum sapien, a ultrices elit. Integer a accumsan nunc. Fusce sed consectetur velit, nec congue dui. Maecenas fermentum scelerisque nulla, non venenatis sapien finibus in. Curabitur euismod, elit sit amet maximus pharetra, sapien turpis rhoncus odio, a lacinia mi urna et nulla. Vivamus suscipit libero vel convallis vestibulum. Curabitur vitae magna ac erat dictum consectetur.\n");
            }
            else
            {
                Iterator<String> iterator = allURLS.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = iterator.next();
                    if (!queryReturns.containsKey(key)) {
                        iterator.remove();
                    }
                }

            }

        }
        return (allURLS);

    }
}
