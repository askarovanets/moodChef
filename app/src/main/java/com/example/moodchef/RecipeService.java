package com.example.moodchef;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RecipeService {
    @GET("recipes/findByIngredients")
    Call<List<RecipeResponse.Recipe>> getRecipesByIngredients(
            @Query("ingredients") String ingredients,
            @Query("apiKey") String apiKey,
            @Query("number") int number
    );

    @GET("recipes/{id}/information")
    Call<RecipeDetailResponse> getRecipeInformation(
            @Path("id") int recipeId,
            @Query("apiKey") String apiKey
    );
}
