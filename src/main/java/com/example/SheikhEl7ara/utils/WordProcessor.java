package com.example.SheikhEl7ara.utils;


import org.tartarus.snowball.ext.EnglishStemmer;
import java.util.Arrays;
import java.util.List;

public class WordProcessor {
    private final List<String> stopWords;
    public WordProcessor(){
        stopWords = Arrays.asList(
                "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
                "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
                "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
                "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
                "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in",
                "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the",
                "their", "then", "there", "these", "they", "this", "to", "was", "will",
                "with"
        );
    }
    public String changeWordToLowercase(String word){
        return word.toLowerCase();
    }
    public String removeStopWords(String word) {
        if (!stopWords.contains(word))
            return word;
        return "";
    }
    public String wordStemmer(String word) {
        EnglishStemmer stemmer = new EnglishStemmer();
        stemmer.setCurrent(word);
        stemmer.stem();
        // System.out.println(word+" : "+stemmer.getCurrent());
        return stemmer.getCurrent();
    }
}