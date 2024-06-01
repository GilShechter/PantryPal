package com.pantrypal.pantrypal.controller;

import com.pantrypal.pantrypal.jwt.DBUserService;
import com.pantrypal.pantrypal.model.UserRecipe;
import com.pantrypal.pantrypal.repo.RecipeInformationService;
import com.pantrypal.pantrypal.repo.UserRecipeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;

@RestController
@RequestMapping("/api/user")
public class UserRecipeController implements Serializable {
    @Autowired
    UserRecipeService userRecipeService;

    @Autowired
    RecipeInformationService recipeInformationService;

    @Autowired
    DBUserService userService;

    @RequestMapping(value = "/{userId}/view", method = RequestMethod.POST)
    public ResponseEntity<?> viewRecipe(@PathVariable Long userId, @RequestParam int recipeId) {
        try {
            UserRecipe user = userService.viewRecipe(userId, recipeId);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/{userId}/like", method = RequestMethod.POST)
    public ResponseEntity<?> likeRecipe(@PathVariable Long userId, @RequestParam int recipeId) {
        try {
            UserRecipe user = userService.likeRecipe(userId, recipeId);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/{userId}/unlike", method = RequestMethod.POST)
    public ResponseEntity<?> unlikeRecipe(@PathVariable Long userId, @RequestParam int recipeId) {
        try {
            UserRecipe user = userService.unlikeRecipe(userId, recipeId);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/{userId}/viewed", method = RequestMethod.GET)
    public ResponseEntity<?> getRecentlyViewedRecipes(@PathVariable Long userId) {
        try {
            return new ResponseEntity<>(userService.getViewedRecipes(userId), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @RequestMapping(value = "/{userId}/liked", method = RequestMethod.GET)
    public ResponseEntity<?> getLikedRecipes(@PathVariable Long userId) {
        try {
            return new ResponseEntity<>(userService.getLikedRecipes(userId), HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

}
