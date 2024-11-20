package com.example.moodchef;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import java.util.List;

public interface RecipeService {
    @GET("recipes/findByIngredients")
    Call<List<RecipeResponse.Recipe>> getRecipesByIngredients(
            @Query("ingredients") String ingredients,
            @Query("apiKey") String apiKey,
            @Query("number") int number // limit number of recipes
    );
}
