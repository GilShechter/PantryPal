package com.pantrypal.pantrypal.repo;

import com.pantrypal.pantrypal.model.UserRecipe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserRecipeService {
    @Autowired
    UserRecipeRepository repository;

    public Iterable<UserRecipe> findAll() {
        return repository.findAll();
    }

    public Optional<UserRecipe> findById(Long id) {
        return repository.findById(id);
    }

    public UserRecipe save(UserRecipe userRecipe) {
        return repository.save(userRecipe);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
