package com.pantrypal.pantrypal.jwt;


import com.pantrypal.pantrypal.model.UserRecipe;
import com.pantrypal.pantrypal.repo.RecipeInformationRepository;
import com.pantrypal.pantrypal.repo.UserRecipeRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

@Service
public class DBUserService {


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DBUserService.class);

    @Autowired
    private DBUserRepository userRepository;

    @Autowired
    private RecipeInformationRepository recipeRepository;

    @Autowired
    private UserRecipeRepository userRecipeRepository;

    public Optional<DBUser> findUserName(String userName) {
            return userRepository.findByName(userName);
    }

    public Optional<DBUser> findUserById(Long id) {
        return userRepository.findById(id);
    }

    public void save(DBUser user) {
        userRepository.save(user);
    }

    public void deleteByUserName(String username) {
        try {
            long userId = userRepository.findByName(username).orElseThrow(() -> new Exception("User: " + username + "not found")).getId();
            userRepository.deleteById(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public UserRecipe viewRecipe(Long userId, int recipeId) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        recipeRepository.findById(recipeId).orElseThrow(() -> new RuntimeException("Recipe not found"));
        UserRecipe userRecipe = userRecipeRepository.findUserRecipeByUserIdAndRecipeId(userId, recipeId);
        if (userRecipe == null) {
            userRecipe = new UserRecipe();
            userRecipe.setUserId(userId);
            userRecipe.setRecipeId(recipeId);
            userRecipe.setCreatedAt(Date.from(java.time.Instant.now()));
        }
        return userRecipeRepository.save(userRecipe);
    }

    public UserRecipe likeRecipe(Long userId, int recipeId) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        recipeRepository.findById(recipeId).orElseThrow(() -> new RuntimeException("Recipe not found"));
        UserRecipe userRecipe = userRecipeRepository.findUserRecipeByUserIdAndRecipeId(userId, recipeId);
        if (userRecipe == null) {
            throw new RuntimeException("UserRecipe not found");
        }
        userRecipe.setLiked(true);
        return userRecipeRepository.save(userRecipe);
    }

    public UserRecipe unlikeRecipe(Long userId, int recipeId) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        recipeRepository.findById(recipeId).orElseThrow(() -> new RuntimeException("Recipe not found"));
        UserRecipe userRecipe = userRecipeRepository.findUserRecipeByUserIdAndRecipeId(userId, recipeId);
        if (userRecipe == null) {
            throw new RuntimeException("UserRecipe not found");
        }
        userRecipe.setLiked(false);
        return userRecipeRepository.save(userRecipe);
    }

    public List<UserRecipe> getViewedRecipes(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return userRecipeRepository.findAllByUserId(userId);
    }

    public List<UserRecipe> getLikedRecipes(Long userId) {
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return userRecipeRepository.findUserRecipeByUserIdAndLikedTrue(userId);
    }
}
