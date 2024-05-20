package com.pantrypal.pantrypal.model;

import javax.persistence.*;
import java.util.List;

@Entity
public class RecipeInformation {

    @Id
    private Integer id;
    private String title;
    private Integer readyInMinutes;
    private String image;

    @Lob
    private String instructions;
    private String sourceUrl;

    @ElementCollection
    @CollectionTable(name = "extended_ingredients", joinColumns = @JoinColumn(name = "recipe_id"))
    @Column(name = "ingredient")
    private List<String> extendedIngredients;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReadyInMinutes() {
        return readyInMinutes;
    }

    public void setReadyInMinutes(Integer readyInMinutes) {
        this.readyInMinutes = readyInMinutes;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public List<String> getExtendedIngredients() {
        return extendedIngredients;
    }

    public void setExtendedIngredients(List<String> extendedIngredients) {
        this.extendedIngredients = extendedIngredients;
    }
}
