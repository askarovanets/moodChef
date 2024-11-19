package com.example.moodchef;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RecipeService {
    @GET("recipes/complexSearch")  // Update this endpoint for Spoonacular or Edamam
    Call<RecipeResponse> getRecipes(
            @Query("query") String mood,
            @Query("apiKey") String apiKey,
            @Query("ingredients") String ingredients
    );
}
