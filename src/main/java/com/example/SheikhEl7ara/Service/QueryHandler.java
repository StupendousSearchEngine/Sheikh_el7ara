package com.example.SheikhEl7ara.Service;
import edu.stanford.nlp.ling.*;
import java.util.List;
import java.util.Properties;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import org.springframework.stereotype.Service;

@Service
public class QueryHandler {
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
}
