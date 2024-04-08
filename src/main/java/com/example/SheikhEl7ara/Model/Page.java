package com.example.SheikhEl7ara.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "pages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page {
    @Id
    private String id;
    @Indexed(unique = true)
    private String canonicalUrl;
    private String url;
    private String title;
}
