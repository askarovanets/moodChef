package com.example.moodchef;

import java.util.List;

public class RecipeDetailResponse {
    private String title;
    private String image;
    private List<ExtendedIngredient> extendedIngredients;
    private String instructions;

    public String getTitle() { return title; }
    public String getImage() { return image; }
    public String getInstructions() { return instructions; }

    public List<ExtendedIngredient> getExtendedIngredients() {
        return extendedIngredients;
    }

    public String getExtendedIngredientsAsString() {
        StringBuilder builder = new StringBuilder();
        for (ExtendedIngredient ingredient : extendedIngredients) {
            builder.append("- ").append(ingredient.getOriginal()).append("\n");
        }
        return builder.toString();
    }

    public static class ExtendedIngredient {
        private String original;

        public String getOriginal() {
            return original;
        }
    }
}