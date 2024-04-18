package com.example.SheikhEl7ara.Controller;

import com.example.SheikhEl7ara.Model.Word;
import com.example.SheikhEl7ara.Service.CrawlerService;
import com.example.SheikhEl7ara.Service.PhraseSearching;
import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/phrase")
public class PhraseController {
    private final PhraseSearching phraseSearching;

    @Autowired
    public PhraseController(PhraseSearching phraseSearching){
        this.phraseSearching=phraseSearching;
    }

    @GetMapping("/{word}")
    public ResponseEntity<Optional<HashMap<String, String>>>
    getWordOccurrences(@PathVariable String word){
        return new ResponseEntity<Optional<HashMap<String,String>>>
                (Optional.ofNullable(phraseSearching.queryParser(word)), HttpStatus.OK);
    }

}
