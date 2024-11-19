package com.example.moodchef;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText moodInput;
    private Button moodSubmitButton;
    private TwinwordEmotionApiClient apiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moodInput = findViewById(R.id.moodInput);
        moodSubmitButton = findViewById(R.id.moodSubmitButton);
        apiClient = new TwinwordEmotionApiClient();

        moodSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userMood = moodInput.getText().toString().trim();
                if (!userMood.isEmpty()) {
                    analyzeMood(userMood);
                } else {
                    Log.d("MainActivity", "Mood input is empty!");
                }
            }
        });
    }

    private void analyzeMood(String text) {
        apiClient.analyzeEmotion(text, new TwinwordEmotionApiClient.ApiCallback() {
            @Override
            public void onSuccess(String jsonResponse) {
                Log.d("TwinwordResponse", "Response: " + jsonResponse);

                try {
                    // Parse the JSON response
                    JSONObject responseObject = new JSONObject(jsonResponse);

                    // Get the array of emotions detected
                    JSONArray emotionsDetected = responseObject.getJSONArray("emotions_detected");

                    // Get the emotion scores object
                    JSONObject emotionScores = responseObject.getJSONObject("emotion_scores");

                    // Find the dominant emotion
                    String dominantEmotion = null;
                    double maxScore = Double.MIN_VALUE;

                    for (int i = 0; i < emotionsDetected.length(); i++) {
                        String emotion = emotionsDetected.getString(i);
                        double score = emotionScores.getDouble(emotion);

                        if (score > maxScore) {
                            maxScore = score;
                            dominantEmotion = emotion;
                        }
                    }

                    Log.d("TwinwordResponse", "Dominant Emotion: " + dominantEmotion);

                    // Suggest recipes based on the dominant emotion
                    suggestRecipes(dominantEmotion);

                } catch (Exception e) {
                    Log.e("TwinwordResponse", "Error parsing JSON: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("TwinwordResponse", "Error: " + e.getMessage());
            }
        });
    }

    private void suggestRecipes(String emotion) {
        if (emotion == null) {
            Log.d("SuggestRecipes", "No dominant emotion detected.");
            return;
        }

        switch (emotion) {
            case "sadness":
                Log.d("SuggestRecipes", "Comfort food like pasta or soup!");
                // You could load actual comfort food recipes here
                break;

            case "anger":
                Log.d("SuggestRecipes", "Spicy dishes to channel the heat!");
                // You could load spicy food recipes here
                break;

            case "fear":
                Log.d("SuggestRecipes", "Soothing teas or light meals!");
                // You could load calming food recipes here
                break;

            case "joy":
                Log.d("SuggestRecipes", "Desserts or refreshing smoothies!");
                // You could load desserts here
                break;

            case "disgust":
                Log.d("SuggestRecipes", "Try something new and exotic!");
                // You could load adventurous recipes here
                break;

            case "surprise":
                Log.d("SuggestRecipes", "Quick and exciting dishes!");
                // You could load exciting recipes here
                break;

            default:
                Log.d("SuggestRecipes", "General recipes for any mood.");
                // Load general recipes here
                break;
        }
    }
}
