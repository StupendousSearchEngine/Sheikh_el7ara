package com.example.SheikhEl7ara.Repository;

import com.example.SheikhEl7ara.Model.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PageRepository extends MongoRepository<Page, String> {
    Page findByNormlizedUrl(String normlizedUrl);
    @Query("{ 'isIndexed' : false }")
    List<Page> findUnindexedPages();
}
