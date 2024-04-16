package com.example.SheikhEl7ara.Model;

import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Document(collection = "words")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter

public class Word {

    @Id
    private String id;

    @Indexed(unique = true)
    private String word;

    private HashMap<String, ArrayList<Double>> TF_IDFandOccurrences;

    public Word(String word, HashMap<String,ArrayList<Double>> occurrences) {
        this.word = word;
        this.TF_IDFandOccurrences = occurrences;
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

    public HashMap<String, ArrayList<Double>> getOccurrences() {
        return TF_IDFandOccurrences;
    }



}
