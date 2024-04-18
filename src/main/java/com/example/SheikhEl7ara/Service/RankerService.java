package com.example.SheikhEl7ara.Service;
import com.example.SheikhEl7ara.Model.Page;
import com.example.SheikhEl7ara.Model.Word;
import com.example.SheikhEl7ara.Repository.PageRepository;
import com.example.SheikhEl7ara.Repository.WordRepository;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
@Service
public class RankerService {
    private final PageRepository pageRepository;
    private final WordRepository wordRepository;
    private List<Pair<String,ArrayList<Double>>> sortedPageFinalScore;
    private HashMap<String,ArrayList<Double>> pageTF_IDFScoreHashMap;
    @Autowired
    public RankerService(PageRepository pageRepository,WordRepository wordRepository){
        this.pageRepository = pageRepository;
        this.wordRepository = wordRepository;
    }
    public HashMap<String, ArrayList<Double>> startRanking(String token){
        sortedPageFinalScore = new ArrayList<>();
        pageTF_IDFScoreHashMap = new HashMap<>();
        calculatePageFinalTF_IDF(token);
        calculatePageFinalTotalScore();
        sortPagesByFinalScore();

        // Convert List<Pair<String, ArrayList<Double>>> to HashMap<String, ArrayList<Double>>
        HashMap<String, ArrayList<Double>> resultMap = new HashMap<>();
        for (Pair<String, ArrayList<Double>> pair : sortedPageFinalScore) {
            resultMap.put(pair.getKey(), pair.getValue());
        }

        return resultMap;
    }
    private void calculatePageFinalTF_IDF(String relevantWord) {
        String URL;
        Optional<Word> wordObject = Optional.of(new Word());
        wordObject = wordRepository.findWordByword(relevantWord);
        double TF_IDFScore;
        if (wordObject.isPresent()) { // Check if the Optional contains a value
            Word word = wordObject.get(); // Get the actual object from Optional
            for (Map.Entry<String, ArrayList<Double>> TF_IDFAndOccurrences : word.getTF_IDFandOccurrences().entrySet()) {
                URL = TF_IDFAndOccurrences.getKey().replace("__", ".");
                if (!pageTF_IDFScoreHashMap.containsKey(URL)) {
                    pageTF_IDFScoreHashMap.put(URL, TF_IDFAndOccurrences.getValue());
                } else {
                    TF_IDFScore = pageTF_IDFScoreHashMap.get(URL).get(0) + TF_IDFAndOccurrences.getValue().get(0);
                    ArrayList<Double> arrayList = new ArrayList<>();
                    arrayList = pageTF_IDFScoreHashMap.get(URL);
                    arrayList.set(0, TF_IDFScore);
                    pageTF_IDFScoreHashMap.put(URL, arrayList);
                }
            }

        }
    }
    private void calculatePageFinalTotalScore(){
        Optional<Page> page;
        int pagePopularity;
        double pageFinalScore,pageTF_IDF;
        for(Map.Entry<String,ArrayList<Double>>pairOfURLAndTF_IDF : pageTF_IDFScoreHashMap.entrySet()){
            pageTF_IDF = pairOfURLAndTF_IDF.getValue().get(0);
            page = pageRepository.findByNormlizedUrl(pairOfURLAndTF_IDF.getKey());
            if(page.isPresent()){
                pagePopularity = page.get().getPopularity();
                pageFinalScore = (pagePopularity*pageTF_IDF)/(pagePopularity+pageTF_IDF);
                ArrayList<Double>arrayList=new ArrayList<>();
                arrayList.add(pageFinalScore);
                pairOfURLAndTF_IDF.getValue().remove(0);
                arrayList.addAll(pairOfURLAndTF_IDF.getValue());
                pairOfURLAndTF_IDF.setValue(arrayList);
                Pair<String,ArrayList<Double>> pair = new Pair<>(pairOfURLAndTF_IDF.getKey(),pairOfURLAndTF_IDF.getValue());
                sortedPageFinalScore.add(pair);
            }
        }
    }
    private void sortPagesByFinalScore() {
        sortedPageFinalScore.sort((p1, p2) -> {
            double firstElementOfArray1 = p1.getValue().get(0);
            double firstElementOfArray2 = p2.getValue().get(0);
            return Double.compare(firstElementOfArray2, firstElementOfArray1);
        });
    }    private void printPageFinalScore(){
        for(Pair<String,ArrayList<Double>> pair:sortedPageFinalScore){

            System.out.println(pair.getKey()+"\t"+pair.getValue());
        }
    }
}
