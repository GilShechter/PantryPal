package com.pantrypal.pantrypal.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pantrypal.pantrypal.jwt.DBUserService;
import com.pantrypal.pantrypal.model.RecipeInformation;
import com.pantrypal.pantrypal.model.RecipePreview;
import com.pantrypal.pantrypal.repo.RecipeInformationService;
import com.pantrypal.pantrypal.repo.RecipePreviewService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    RecipePreviewService recipePreviewService;

    @Autowired
    RecipeInformationService recipeInformationService;

    @Autowired
    DBUserService userService;

    @Autowired
    ObjectMapper om;

    @RequestMapping(value = "/searchByIngredients", method = RequestMethod.GET)
    public ResponseEntity<?> searchByIngredients(@RequestParam String ingredients) {
        try{
            String url = targetUrl + "findByIngredients?ingredients=" + ingredients;
            ResponseEntity<?> responseEntity  = executeHttpRequest(url);
            String responseBody = responseEntity.getBody().toString();
            RecipePreview[] recipes = om.readValue(responseBody, RecipePreview[].class);
            return ResponseEntity.ok(recipes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/getRecipeInfo", method = RequestMethod.GET)
    public ResponseEntity<?> getRecipeInfo(@RequestParam int id) {
        try{
            ResponseEntity optionalRecipe = getRecipe(id);
            if (optionalRecipe.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(optionalRecipe);
            }
            String url = targetUrl + id + "/information";
            String responseBody = executeHttpRequest(url).getBody().toString();
            RecipeInformation recipe = mapRecipeInformation(responseBody);
            return ResponseEntity.ok(recipe);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> createRecipe(@RequestBody RecipeInformation recipeInformation) {
        RecipeInformation savedRecipe = recipeInformationService.save(recipeInformation);
        return ResponseEntity.ok(savedRecipe);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRecipe(@PathVariable int id) {
        Optional<RecipeInformation> recipe = recipeInformationService.findById(id);
        if (recipe.isPresent()) {
            return ResponseEntity.ok(recipe.get());
        }
        return ResponseEntity.status(404).body("Recipe not found");
    }

    @GetMapping
    public ResponseEntity<?> getAllRecipes() {
        Iterable<RecipeInformation> recipes = recipeInformationService.findAll();
        return ResponseEntity.ok(recipes);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable int id) {
        recipeInformationService.deleteById(id);
        return ResponseEntity.ok("Recipe id " + id + " deleted successfully");
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

    private RecipeInformation mapRecipeInformation(String responseBody) throws IOException {
        JsonNode node = om.readTree(responseBody);
        RecipeInformation recipe = new RecipeInformation();
        recipe.setId(node.get("id").asInt());
        recipe.setTitle(node.get("title").asText());
        recipe.setReadyInMinutes(node.get("readyInMinutes").asInt());
        recipe.setImage(node.get("image").asText());
        recipe.setInstructions(node.get("instructions").asText());
        recipe.setSourceUrl(node.get("sourceUrl").asText());

        List<String> extendedIngredients = StreamSupport.stream(node.get("extendedIngredients").spliterator(), false)
                .map(ingredientNode -> ingredientNode.get("original").asText())
                .collect(Collectors.toList());
        recipe.setExtendedIngredients(extendedIngredients);

        return recipe;
    }
}
