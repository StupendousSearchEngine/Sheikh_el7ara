package com.example.SheikhEl7ara.Controller;

import com.example.SheikhEl7ara.Model.Word;
import com.example.SheikhEl7ara.Service.WordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("test")
public class WordController {
    @Autowired
    private WordService wordService;
    @GetMapping("/{word}")
    public ResponseEntity<Optional<Word>> getWordOccurrences(@PathVariable String word){
        return new ResponseEntity<Optional<Word>>(wordService.singleWord(word), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Word> appendToWordOccurrences(@RequestBody Map<String,String> payload){
        List<String> list = new ArrayList<>();
        return new ResponseEntity<Word>(wordService.appendOccurrence(payload.get("word"),payload.get("occ")), HttpStatus.CREATED);
    }
}
