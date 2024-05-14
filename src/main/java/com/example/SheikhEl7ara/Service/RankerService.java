package com.example.SheikhEl7ara.Service;
import com.example.SheikhEl7ara.Model.Page;
import com.example.SheikhEl7ara.Model.Word;
import com.example.SheikhEl7ara.Repository.PageRepository;
import com.example.SheikhEl7ara.Repository.WordRepository;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

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
    public HashMap<String, ArrayList<Double>> startRanking(String[] tokens){

        List<Word> wordList = new ArrayList<Word>();
        for (int i = 0; i < tokens.length; i++) {
            System.out.println("debug1");
            wordList.add(wordRepository.findWordByword(tokens[i]));
        }

        sortedPageFinalScore = new ArrayList<>();
        pageTF_IDFScoreHashMap = new HashMap<>();
        calculatePageFinalTF_IDF(wordList);
        calculatePageFinalTotalScore();
        sortPagesByFinalScore();
//        HashMap<String, ArrayList<Double>> resultMap = new HashMap<>();
//        for (Pair<String, ArrayList<Double>> pair : sortedPageFinalScore) {
//            resultMap.put(pair.getKey(), pair.getValue());
//        }

        HashMap<String, ArrayList<Double>> resultMap = sortedPageFinalScore.stream()
                .collect(Collectors.toMap(
                        Pair::getKey, // Key mapper
                        Pair::getValue, // Value mapper
                        (oldValue, newValue) -> oldValue, // Merge function (if duplicate keys exist)
                        HashMap::new // Supplier for the HashMap
                ));

        return resultMap;
    }
    private void calculatePageFinalTF_IDF(List<Word> relevantWords){
        String URL;
        double TF_IDFScore;
        for(Word wordObject:relevantWords){
            System.out.println("debug2");
            for(Map.Entry<String,ArrayList<Double>> TF_IDFAndOccurrences:wordObject.getTF_IDFandOccurrences().entrySet()){
                System.out.println("debug3");
                URL=TF_IDFAndOccurrences.getKey().replace("__",".");
                if(!pageTF_IDFScoreHashMap.containsKey(URL)){
                    pageTF_IDFScoreHashMap.put(URL, TF_IDFAndOccurrences.getValue());                }
                else{
                    TF_IDFScore = pageTF_IDFScoreHashMap.get(URL).get(0) + TF_IDFAndOccurrences.getValue().get(0);
                    ArrayList<Double> arrayList = new ArrayList<>();
                    arrayList = pageTF_IDFScoreHashMap.get(URL);
                    arrayList.set(0, TF_IDFScore);
                    pageTF_IDFScoreHashMap.put(URL, arrayList);
                }
            }
        }
    }
    private void calculatePageFinalTotalScore() {
        ConcurrentLinkedQueue<Pair<String, ArrayList<Double>>> concurrentSortedPageFinalScore = new ConcurrentLinkedQueue<>();

        pageTF_IDFScoreHashMap.entrySet().parallelStream().forEach(pairOfURLAndTF_IDF -> {
            System.out.println("debug4");
            double pageTF_IDF = pairOfURLAndTF_IDF.getValue().get(0);
            Page page = pageRepository.findByNormlizedUrl(pairOfURLAndTF_IDF.getKey());
            if (page != null) {
                int pagePopularity = page.getPopularity();
                double pageFinalScore = pageTF_IDF;
                ArrayList<Double> arrayList = new ArrayList<>();
                arrayList.add(pageFinalScore);
                pairOfURLAndTF_IDF.getValue().remove(0);
                arrayList.addAll(pairOfURLAndTF_IDF.getValue());
                pairOfURLAndTF_IDF.setValue(arrayList);
                Pair<String, ArrayList<Double>> pair = new Pair<>(pairOfURLAndTF_IDF.getKey(), pairOfURLAndTF_IDF.getValue());
                concurrentSortedPageFinalScore.add(pair);
            }
        });

        sortedPageFinalScore = new ArrayList<>(concurrentSortedPageFinalScore);
    }

    private void sortPagesByFinalScore() {
        sortedPageFinalScore.sort((p1, p2) -> {
            double firstElementOfArray1 = p1.getValue().get(0);
            double firstElementOfArray2 = p2.getValue().get(0);
            return Double.compare(firstElementOfArray2, firstElementOfArray1);
        });
    }
    private void printPageFinalScore(){
        for(Pair<String,ArrayList<Double>> pair:sortedPageFinalScore){

            System.out.println(pair.getKey()+"\t"+pair.getValue());
        }
    }
}