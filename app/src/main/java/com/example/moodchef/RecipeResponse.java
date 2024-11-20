package com.example.moodchef;

import java.util.List;

public class RecipeResponse {
    private List<Recipe> results;

    public List<Recipe> getResults() {
        return results;
    }

    public static class Recipe {
        private String title;
        private String image;
        private String sourceUrl;

        // Constructor
        public Recipe(String title, String image, String sourceUrl) {
            this.title = title;
            this.image = image;
            this.sourceUrl = sourceUrl;
        }

        // Getters
        public String getTitle() {
            return title;
        }

        public String getImage() {
            return image;
        }

        public String getSourceUrl() {
            return sourceUrl;
        }
    }
}
