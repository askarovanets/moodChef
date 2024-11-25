package com.example.moodchef;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        recipeFetchButton.setOnClickListener(v -> {
            String userMoodText = moodInput.getText().toString().trim();
            String userIngredients = ingredientInput.getText().toString().trim();

            if (userMoodText.isEmpty() || userIngredients.isEmpty()) {
                recipeSuggestionText.setText("Please enter both mood and ingredients!");
                return;
            }

            analyzeMood(userMoodText, emotions -> fetchRecipes(userIngredients, emotions));
        });
    }

    private void analyzeMood(String text, MoodCallback callback) {
        new TwinwordEmotionApiClient().analyzeEmotion(text, new TwinwordEmotionApiClient.ApiCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                List<String> emotions = parseEmotions(jsonResponse);
                callback.onMoodDetected(emotions.isEmpty() ? List.of("neutral") : emotions);
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("TwinwordResponse", "Error: " + e.getMessage());
                callback.onMoodDetected(List.of("neutral"));
            }
        });
    }

    private List<String> parseEmotions(String jsonResponse) {
        List<String> emotions = new ArrayList<>();
        try {
            JSONObject responseObject = new JSONObject(jsonResponse);
            JSONArray emotionsDetected = responseObject.getJSONArray("emotions_detected");
            for (int i = 0; i < emotionsDetected.length(); i++) {
                emotions.add(emotionsDetected.getString(i));
            }
        } catch (Exception e) {
            Log.e("ParseError", "Error parsing emotions: " + e.getMessage());
        }
        return emotions;
    }

    private void fetchRecipes(String ingredients, List<String> emotions) {
        String modifiedIngredients = addMoodBasedModifiers(ingredients, emotions);

        RetrofitInstance.getRecipeApi().getRecipesByIngredients(modifiedIngredients, BuildConfig.SPOONACULAR_API_KEY, 20)
                .enqueue(new Callback<List<RecipeResponse.Recipe>>() {
                    @Override
                    public void onResponse(Call<List<RecipeResponse.Recipe>> call, Response<List<RecipeResponse.Recipe>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            displayRecipes(filterRecipes(response.body(), new HashSet<>(emotions)));
                        } else {
                            recipeSuggestionText.setText("No recipes found!");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RecipeResponse.Recipe>> call, Throwable t) {
                        recipeSuggestionText.setText("Failed to fetch recipes.");
                    }
                });
    }

    private String addMoodBasedModifiers(String ingredients, List<String> emotions) {
        StringBuilder modifiedIngredients = new StringBuilder(ingredients);
        for (String emotion : emotions) {
            switch (emotion.toLowerCase()) {
                case "joy":
                    modifiedIngredients.append(",fruit,smoothie");
                    break;
                case "anger":
                    modifiedIngredients.append(",spicy,hot");
                    break;
                case "sad":
                    modifiedIngredients.append(",comfort,cheese");
                    break;
                case "fear":
                    modifiedIngredients.append(",calm,soothing");
                    break;
                case "disgust":
                    modifiedIngredients.append(",cleanse,refreshing");
                    break;
                case "surprise":
                    modifiedIngredients.append(",unique,exotic");
                    break;
            }
        }
        return modifiedIngredients.toString();
    }


    private List<RecipeResponse.Recipe> filterRecipes(List<RecipeResponse.Recipe> recipes, HashSet<String> emotions) {
        List<RecipeResponse.Recipe> filteredRecipes = new ArrayList<>();
        for (RecipeResponse.Recipe recipe : recipes) {
            String title = recipe.getTitle().toLowerCase();
            if (emotions.contains("joy") && title.contains("smoothie")) filteredRecipes.add(recipe);
            else if (emotions.contains("anger") && title.contains("spicy")) filteredRecipes.add(recipe);
            else if (emotions.contains("sad") && title.contains(",comfort,cheese")) filteredRecipes.add(recipe);
            else if (emotions.contains("fear") && title.contains(",calm,soothing")) filteredRecipes.add(recipe);
            else if (emotions.contains("disgust") && title.contains(",cleanse,refreshing")) filteredRecipes.add(recipe);
            else if (emotions.contains("surprise") && title.contains(",unique,exotic")) filteredRecipes.add(recipe);
        }
        return filteredRecipes.isEmpty() ? recipes : filteredRecipes;
    }

    private void displayRecipes(List<RecipeResponse.Recipe> recipes) {
        if (recipeAdapter == null) {
            recipeAdapter = new RecipeAdapter(recipes);
            recipeRecyclerView.setAdapter(recipeAdapter);
        } else {
            recipeAdapter.updateRecipes(recipes);
        }
    }

    public interface MoodCallback {
        void onMoodDetected(List<String> emotions);
    }
}
