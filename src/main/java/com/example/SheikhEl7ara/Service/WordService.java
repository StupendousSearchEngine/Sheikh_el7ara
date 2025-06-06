package com.example.SheikhEl7ara;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WordService {
    @Autowired
    WordRepository wordRepository;

    public Optional<Word>  singleWord(String word){
        return wordRepository.findWordByword(word);
    }

    public Word appendOccurrence(String wordStr, String occurrence) {
        // Find the Word object with the given word
        Optional<Word> word = wordRepository.findWordByword(wordStr);

        if (word.isPresent()) {
            Word acutualWord = word.get();
            // Append the occurrence to the occurrences list
            List<String> occurrences = acutualWord.getOccurrences();
            occurrences.add(occurrence);
            acutualWord.setOccurrences(occurrences);

            // Save the updated Word object
            wordRepository.save(acutualWord);
        } else {
            Word newWord = new Word(wordStr, new ArrayList<>());
            newWord.getOccurrences().add(occurrence);
            wordRepository.save(newWord);
        }
        Word worrd = wordRepository.findWordByword(wordStr).get();
        return worrd;
    }
}
