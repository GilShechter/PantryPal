package com.pantrypal.pantrypal.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IngredientList {
    private List<String> ingredients;

    public IngredientList() {
        this.ingredients = new ArrayList<>();
    }

    public IngredientList(List<String> ingredients) {
        ArrayList<String> ingredientsArrayList = new ArrayList<>(ingredients);
        this.ingredients = ingredientsArrayList;
    }

    public IngredientList(String ingredients) {
        List<String> ingredientsList = Arrays.asList(ingredients.split(" "));
        ArrayList<String> ingredientsArrayList = new ArrayList<>(ingredientsList);
        this.ingredients = ingredientsArrayList;
    }

    @Override
    public String toString() {
        return String.join(",", ingredients);
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}

