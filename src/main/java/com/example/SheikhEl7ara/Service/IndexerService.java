package com.example.SheikhEl7ara.Service;
import com.example.SheikhEl7ara.Model.Page;
import com.example.SheikhEl7ara.Model.Word;
import com.example.SheikhEl7ara.Repository.PageRepository;
import com.example.SheikhEl7ara.Repository.WordRepository;
import com.example.SheikhEl7ara.utils.WordProcessor;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IndexerService {
    private int numThreads;
    private int totalNoOfPages;
    private final WordRepository wordRepository;
    private final PageRepository pageRepository;
    private static final ConcurrentHashMap<String, HashMap<String, ArrayList<Double>>> invertedIndex = new ConcurrentHashMap<>();

    @Autowired
    public IndexerService(WordRepository wordRepository,PageRepository pageRepository){
        this.pageRepository = pageRepository;
        this.wordRepository = wordRepository;
    }
    public void setIndexerThreads(int numThreads) {
        this.numThreads = numThreads;
    }

    public void startIndexing(){
        List<Page> pageList = pageRepository.findUnindexedPages();
        totalNoOfPages = pageList.size();
        System.out.println("Started Indexing " + totalNoOfPages + " pages");
        if(pageList.isEmpty()) return;

        Thread[] threads = new Thread[numThreads];

        for (int i = 0; i < numThreads; i++) {
            int start = i * pageList.size() / numThreads;
            int end = (i + 1) * pageList.size() / numThreads;
            threads[i] = new Thread(new IndexerThread(pageList.subList(start, end)));
            threads[i].setName(Integer.toString(i));
            threads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Thread[] scoringThreads = new Thread[numThreads];

        ArrayList<String> invertedIndexWords = new ArrayList<>(invertedIndex.keySet());

        for (int i = 0; i < numThreads; i++) {
            int start = i * invertedIndexWords.size() / numThreads;
            int end = (i + 1) * invertedIndexWords.size() / numThreads;
            scoringThreads[i] = new Thread(new ScoringThread(invertedIndexWords.subList(start, end)));
            scoringThreads[i].setName(Integer.toString(i));
            scoringThreads[i].start();
        }

        for (int i = 0; i < numThreads; i++) {
            try {
                scoringThreads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private class IndexerThread implements Runnable {
        private int totalNoWordsInADocument = 0;
        private final List<Page> pageList;
        private final List<String> words = new ArrayList<>();
        private final WordProcessor wordProcessor = new WordProcessor();
        private final HashMap<String, Integer> wordFrequency = new HashMap<>();

        public  IndexerThread(List<Page> pageList) {
            this.pageList = pageList;
        }



        public void run() {
            for (Page currPage : pageList) {
                try {

                    Connection connect = Jsoup.connect(currPage.getUrl());
                    if(connect==null)continue;
                   Document  document = connect.get();

                String html = document.html();
                if (html == null) return;
                String URL = currPage.getNormlizedUrl();
                URL = URL.replace(".", "__");
                Matcher matcher = createMatcherFromHTML(html);
                processWords(matcher, URL);
                calculateTF(URL);
                currPage.setIndexed(true);
                } catch (IOException e) {
                  e.printStackTrace();
                }
            }
            // Update the isIndexed flag of the indexed pages
            pageRepository.saveAll(pageList);
        }

        private void processWords(Matcher matcher,String URL) {
            double position=0;
            while (matcher.find()) {
                totalNoWordsInADocument++;
                String word = wordProcessor.changeWordToLowercase(matcher.group()).toLowerCase();
                if (!Objects.equals(wordProcessor.removeStopWords(word), "")) {
                    word = wordProcessor.wordStemmer(word);
                    calculateWordFrequency(word);
                    addToInvertedIndex(word, URL, position);
                    words.add(word);
                }
                position++;
            }
        }

        private Matcher createMatcherFromHTML(String htmlContent) {
            htmlContent = htmlContent.replaceAll("<[^>]*>", "");
            Pattern pattern = Pattern.compile("\\w+");
            return pattern.matcher(htmlContent);
        }
        private void calculateWordFrequency(String word) {
            if (wordFrequency.containsKey(word))
                wordFrequency.put(word, wordFrequency.get(word) + 1);
            else wordFrequency.put(word, 1);
        }

        private void calculateTF(String URL) {
            for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
                double TF = (double) entry.getValue() / totalNoWordsInADocument;
                Optional<ArrayList<Double>> positionList = Optional.ofNullable(invertedIndex.get(entry.getKey())).map(map -> map.get(URL));
                positionList.ifPresent(list -> list.set(0, TF));
            }
        }

        private void addToInvertedIndex(String word, String URL,double position) {
            if (invertedIndex.containsKey(word)) {
                if(invertedIndex.get(word).containsKey(URL)){
                    invertedIndex.get(word).get(URL).add(position);
                }
                else {
                    ArrayList<Double> tempWordList=new ArrayList<>();
                    tempWordList.add(0.0);
                    tempWordList.add(position);
                    invertedIndex.get(word).put(URL, tempWordList);
                }
            } else {
                HashMap<String, ArrayList<Double>> wordInnerMap = new HashMap<>();
                ArrayList<Double> tempWordList=new ArrayList<>();
                tempWordList.add(0.0);
                tempWordList.add(position);
                wordInnerMap.put(URL, tempWordList);
                invertedIndex.put(word, wordInnerMap);

            }
        }

        private void print() {
            System.out.println("The current thread is " + Thread.currentThread().getName());
            System.out.println("Words are " + words);
            System.out.println("Word Frequency" + wordFrequency);
            System.out.println("Inverted index " + invertedIndex);
        }
    }
    private class ScoringThread implements  Runnable{
        private List<String> invertedIndexWordsList;
        public ScoringThread(List<String> invertedIndexWordsList){
            this.invertedIndexWordsList=invertedIndexWordsList;
        }
        public void run() {
            String word, documentName;
            HashMap<String, ArrayList<Double>> innerMap;
            double IDF, TF;
            for (String invertedIndexWord : invertedIndexWordsList) {
                word = invertedIndexWord;

                innerMap = invertedIndex.get(word);
                IDF = Math.log(totalNoOfPages / (double) innerMap.size());
                for (Map.Entry<String, ArrayList<Double>> document : innerMap.entrySet()) {
                    documentName = document.getKey();
                    TF = document.getValue().get(0);
                    invertedIndex.get(word).get(documentName).set(0, TF * IDF);
                }
                HashMap<String,Double> tf_idf;
                Optional<Word> existingWord = wordRepository.findWordByword(word);
                if (existingWord.isPresent()) {
                    existingWord.get().setTF_IDFandOccurrences(invertedIndex.get(word));
                    wordRepository.save(existingWord.get());
                } else {
                    Word wordInDB = Word.builder().word(word).TF_IDFandOccurrences(invertedIndex.get(word)).build();
                    wordRepository.save(wordInDB);
                }
            }
        }

    }
}


