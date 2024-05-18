package com.pantrypal.pantrypal.controller;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @RequestMapping(value = "/searchByIngredients", method = RequestMethod.GET)
    public ResponseEntity<?> searchByIngredients(@RequestParam String ingredients) {
        try{
            String url = targetUrl + "findByIngredients?ingredients=" + ingredients;
            return executeHttpRequest(url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/getRecipeInfo", method = RequestMethod.GET)
    public ResponseEntity<?> getRecipeInfo(@RequestParam String id) {
        try{
            String url = targetUrl + id + "/information";
            return executeHttpRequest(url);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @NotNull
    private ResponseEntity<?> executeHttpRequest(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = buildRequest(url);
        Response response = client.newCall(request).execute();
        return ResponseEntity.ok(response.body().string());
    }

    private Request buildRequest(String url) {
        return new Request.Builder()
                .url(url)
                .get()
                .addHeader("X-RapidAPI-Key", apiKey)
                .addHeader("X-RapidAPI-Host", apiHost)
                .build();
    }
}
