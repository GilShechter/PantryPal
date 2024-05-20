package com.pantrypal.pantrypal.repo;

import com.pantrypal.pantrypal.model.RecipeInformation;
import org.springframework.data.repository.CrudRepository;

public interface RecipeInformationRepository extends CrudRepository<RecipeInformation, Integer> {
}
