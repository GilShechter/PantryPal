package com.pantrypal.pantrypal.repo;

import com.pantrypal.pantrypal.model.RecipeInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecipeInformationService {

    @Autowired
    private RecipeInformationRepository recipeInformationRepository;

    public RecipeInformation save(RecipeInformation recipeInformation) {
        return recipeInformationRepository.save(recipeInformation);
    }

    public Iterable<RecipeInformation> findAll() {
        return recipeInformationRepository.findAll();
    }

    public Optional<RecipeInformation> findById(int id) {
        return recipeInformationRepository.findById(id);
    }

    public void deleteById(int id) {
        recipeInformationRepository.deleteById(id);
    }
}

