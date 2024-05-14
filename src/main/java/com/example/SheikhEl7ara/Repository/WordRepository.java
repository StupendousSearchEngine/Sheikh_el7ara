package com.example.SheikhEl7ara.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.SheikhEl7ara.Model.Word;

public interface WordRepository extends MongoRepository<Word,String> {
    Word findWordByword(String imdbId);
}
