package com.example.moodchef;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static final String MOOD_URL = "https://api.paralleldots.com/";  // Base URL for mood API
    private static final String RECIPE_URL = "https://api.spoonacular.com/";  // Base URL for Spoonacular API

    private static Retrofit retrofitMood;
    private static Retrofit retrofitRecipe;

    public static RecipeService getRecipeApi() {
        if (retrofitRecipe == null) {
            retrofitRecipe = new Retrofit.Builder()
                    .baseUrl(RECIPE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitRecipe.create(RecipeService.class);
    }
}
