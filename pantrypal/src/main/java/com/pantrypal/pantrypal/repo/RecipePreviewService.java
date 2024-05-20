package com.pantrypal.pantrypal.repo;

import com.pantrypal.pantrypal.model.RecipePreview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecipePreviewService {

    @Autowired
    RecipePreviewRepository recipePreviewRepository;

    public Iterable<RecipePreview> getAllRecipePreviews() {
        return recipePreviewRepository.findAll();
    }

    public Optional<RecipePreview> getRecipePreviewById(int id) {
        return recipePreviewRepository.findById(id);
    }

    public RecipePreview saveRecipePreview(RecipePreview recipePreview) {
        return recipePreviewRepository.save(recipePreview);
    }

    public void deleteRecipePreview(int id) {
        recipePreviewRepository.deleteById(id);
    }

}
