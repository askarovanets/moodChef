package com.example.moodchef;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;  // Import for ArrayList
import java.util.List;       // Import for List


public class MainActivity extends AppCompatActivity {

    private EditText moodInput, ingredientInput;
    private Button recipeFetchButton;
    private TextView recipeSuggestionText;
    private RecyclerView recipeRecyclerView;
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moodInput = findViewById(R.id.moodInput);
        ingredientInput = findViewById(R.id.ingredientInput);
        recipeFetchButton = findViewById(R.id.recipeFetchButton);
        recipeSuggestionText = findViewById(R.id.recipeSuggestionText);
        recipeRecyclerView = findViewById(R.id.recipeRecyclerView);

        recipeRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        recipeFetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMoodText = moodInput.getText().toString().trim();
                String userIngredients = ingredientInput.getText().toString().trim();

                if (userMoodText.isEmpty() || userIngredients.isEmpty()) {
                    recipeSuggestionText.setText("Please enter both mood and ingredients!");
                    return;
                }

                // First, analyze the mood
                analyzeMood(userMoodText, new MoodCallback() {
                    @Override
                    public void onMoodDetected(String mood) {
                        // After mood is detected, fetch recipes based on ingredients and mood
                        fetchRecipes(userIngredients, mood);
                    }
                });
            }
        });


    }

    public interface MoodCallback {
        void onMoodDetected(String mood);
    }

    private void analyzeMood(String text, final MoodCallback callback) {
        TwinwordEmotionApiClient apiClient = new TwinwordEmotionApiClient();

        apiClient.analyzeEmotion(text, new TwinwordEmotionApiClient.ApiCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                Log.d("TwinwordResponse", "Response: " + jsonResponse);

                try {
                    JSONObject responseObject = new JSONObject(jsonResponse);
                    JSONArray emotionsDetected = responseObject.getJSONArray("emotions_detected");

                    if (emotionsDetected.length() > 0) {
                        String detectedMood = emotionsDetected.getString(0); // Get the primary mood
                        Log.d("Mood", "Detected mood: " + detectedMood);

                        callback.onMoodDetected(detectedMood);
                    } else {
                        Log.e("Mood", "No mood detected");
                        callback.onMoodDetected("neutral"); // Default to neutral if no mood detected
                    }
                } catch (Exception e) {
                    Log.e("TwinwordResponse", "Error parsing JSON: " + e.getMessage());
                    callback.onMoodDetected("neutral");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("TwinwordResponse", "Error: " + e.getMessage());
                callback.onMoodDetected("neutral");
            }
        });
    }

    private List<RecipeResponse.Recipe> filterRecipesByMood(List<RecipeResponse.Recipe> recipes, String mood) {
        List<RecipeResponse.Recipe> filteredRecipes = new ArrayList<>();

        for (RecipeResponse.Recipe recipe : recipes) {
            if (mood.equalsIgnoreCase("happy") && recipe.getTitle().toLowerCase().contains("smoothie")) {
                filteredRecipes.add(recipe);
            } else if (mood.equalsIgnoreCase("sad") && recipe.getTitle().toLowerCase().contains("comfort")) {
                filteredRecipes.add(recipe);
            } else if (mood.equalsIgnoreCase("tired") && recipe.getTitle().toLowerCase().contains("quick")) {
                filteredRecipes.add(recipe);
            }
        }

        // If no specific mood-based recipes are found, return the default list
        return filteredRecipes.isEmpty() ? recipes : filteredRecipes;
    }


    private void fetchRecipes(String ingredients, String mood) {
        RecipeService recipeService = RetrofitInstance.getRecipeApi();

        recipeService.getRecipesByIngredients(ingredients, BuildConfig.SPOONACULAR_API_KEY, 10)
                .enqueue(new Callback<List<RecipeResponse.Recipe>>() {
                    @Override
                    public void onResponse(Call<List<RecipeResponse.Recipe>> call, Response<List<RecipeResponse.Recipe>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            List<RecipeResponse.Recipe> recipes = response.body();

                            // Filter recipes based on mood
                            List<RecipeResponse.Recipe> moodBasedRecipes = filterRecipesByMood(recipes, mood);

                            Log.d("Recipe API", "Mood-based recipes fetched: " + moodBasedRecipes.size());
                            displayRecipes(moodBasedRecipes);
                        } else {
                            Log.e("Recipe API", "No recipes found!");
                            recipeSuggestionText.setText("No recipes found!");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RecipeResponse.Recipe>> call, Throwable t) {
                        Log.e("Recipe API Error", t.getMessage());
                        recipeSuggestionText.setText("Failed to fetch recipes.");
                    }
                });
    }



    private void displayRecipes(List<RecipeResponse.Recipe> recipes) {
        if (recipeAdapter == null) {
            recipeAdapter = new RecipeAdapter(recipes);
            recipeRecyclerView.setAdapter(recipeAdapter);
        } else {
            // Clear old data and update the adapter with new data
            recipeAdapter.updateRecipes(recipes);
        }
    }

}
