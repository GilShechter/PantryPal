package com.pantrypal.pantrypal.repo;

import com.pantrypal.pantrypal.model.UserRecipe;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRecipeRepository extends CrudRepository<UserRecipe, Long> {

    UserRecipe findUserRecipeByUserIdAndRecipeId(Long userId, int recipeId);

    List<UserRecipe> findUserRecipeByUserIdAndLikedTrue(Long userId);

    List<UserRecipe> findAllByUserId(Long userId);
}
