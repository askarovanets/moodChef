package com.example.moodchef;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

public class RecipeDetailActivity extends AppCompatActivity {

    private ImageView recipeImageView;
    private TextView recipeTitleTextView, recipeIngredientsTextView, recipeInstructionsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        recipeImageView = findViewById(R.id.recipeImageView);
        recipeTitleTextView = findViewById(R.id.recipeTitleTextView);
        recipeIngredientsTextView = findViewById(R.id.recipeIngredientsTextView);
        recipeInstructionsTextView = findViewById(R.id.recipeInstructionsTextView);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra("imageUrl");
        String title = intent.getStringExtra("title");
        String ingredients = intent.getStringExtra("ingredients");
        String instructions = intent.getStringExtra("instructions");

        recipeTitleTextView.setText(title);

        recipeIngredientsTextView.setText(!TextUtils.isEmpty(ingredients)
                ? ingredients
                : "Ingredients not available");

        recipeInstructionsTextView.setText(!TextUtils.isEmpty(instructions)
                ? instructions
                : "Instructions not available");

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(recipeImageView);
    }
}
