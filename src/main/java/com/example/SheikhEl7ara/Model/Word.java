package com.example.SheikhEl7ara;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "words")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Word {

    @Id
    private String id;

    @Indexed(unique = true) // Index for fast lookup
    private String word;

    private List<String> occurrences;

    public Word(String word, List<String> occurrences) {
        this.word = word;
        this.occurrences = occurrences;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<String> getOccurrences() {
        return occurrences;
    }

    public void setOccurrences(List<String> occurrences) {
        this.occurrences = occurrences;
    }


}
