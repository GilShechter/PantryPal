package com.pantrypal.pantrypal.controller;

import com.pantrypal.pantrypal.model.IngredientList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
@RequestMapping("/api/recipes")
public class RecipesController {

    @Value("${recipes.api.target.url}")
    private String targetUrl;

    @Value("${recipes.api.key}")
    private String apiKey;

    @Value("${recipes.api.host}")
    private String apiHost;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/searchByIngredients", method = RequestMethod.GET)
    public ResponseEntity<?> searchByIngredients(@RequestParam String ingredients) {
        try{
            String url = targetUrl + ingredients;
            System.out.println(url);
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("X-RapidAPI-Key", apiKey)
                    .addHeader("X-RapidAPI-Host", apiHost)
                    .build();

            Response response = client.newCall(request).execute();
            return ResponseEntity.ok(response.body().string());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
