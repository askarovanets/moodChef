package com.example.moodchef;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {

    private static final String BASE_URL = "https://api.paralleldots.com/";  // Replace with your API base URL
    private static final String RECIPE_URL = "https://api.spoonacular.com/";  // Replace as needed

    private static Retrofit retrofitMood;
    private static Retrofit retrofitRecipe;

    public static MoodService getMoodApi() {
        if (retrofitMood == null) {
            retrofitMood = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitMood.create(MoodService.class);
    }

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
