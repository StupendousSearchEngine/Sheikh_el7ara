package com.example.SheikhEl7ara.Controller;

//<<<<<<< HEAD
//import com.example.SheikhEl7ara.Word;
//=======
//import com.example.SheikhEl7ara.Model.Word;
//>>>>>>> origin/web-with-tested-api
import org.springframework.http.HttpStatus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.web.bind.annotation.CrossOrigin;
@RestController

@RequestMapping("api")
public class apiController {
    @CrossOrigin(origins = "http://127.0.0.1:5500")
    @GetMapping("/{count}")
    public static ResponseEntity<String> generateResponse(@PathVariable int count) {
        List<DataItem> data = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            data.add(new DataItem("What is Lorem Ipsum?", "Where does it come from?\n" +
                    "Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of \"de Finibus Bonorum et Malorum\" (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, \"Lorem ipsum dolor sit amet..\", comes from a line in section 1.10.32.\n" +
                    "\n" +
                    "The standard chunk of Lorem Ipsum used since the 1500s is reproduced below for those interested. Sections 1.10.32 and 1.10.33 from \"de Finibus Bonorum et Malorum\" by Cicero are also reproduced in their exact original form, accompanied by English versions from the 1914 translation by H. Rackham.","https://www.google.com/"));
        }
        ResponseData responseData = new ResponseData(count, data);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(responseData);
        return new ResponseEntity<>(json,HttpStatus.OK);
    }
    static class DataItem {
        private String title;
        private String url;
        private String body;

        public DataItem(String title, String body, String url) {
            this.title = title;
            this.body = body;
            this.url = url;
        }
    }

    static class ResponseData {
        private int count;
        private List<DataItem> data;

        public ResponseData(int count, List<DataItem> data) {
            this.count = count;
            this.data = data;
        }
    }
}
