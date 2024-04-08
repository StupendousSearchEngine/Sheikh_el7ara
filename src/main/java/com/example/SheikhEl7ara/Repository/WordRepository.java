package com.example.SheikhEl7ara.Repository;

import com.example.SheikhEl7ara.Model.Word;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface WordRepository extends MongoRepository<Word,String> {
    Optional<Word> findWordByword(String imdbId);
}
