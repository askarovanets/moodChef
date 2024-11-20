package com.example.moodchef;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<RecipeResponse.Recipe> recipeList;

    public RecipeAdapter(List<RecipeResponse.Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_item, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        RecipeResponse.Recipe recipe = recipeList.get(position);
        holder.recipeTitle.setText(recipe.getTitle());

        String description = recipe.getSourceUrl() != null ? recipe.getSourceUrl() : "No description available";
        holder.recipeDescription.setText(description);

        Glide.with(holder.itemView.getContext())
                .load(recipe.getImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.recipeImage);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    // New method to update recipes
    public void updateRecipes(List<RecipeResponse.Recipe> newRecipes) {
        this.recipeList.clear();
        this.recipeList.addAll(newRecipes);
        notifyDataSetChanged();
    }

    public static class RecipeViewHolder extends RecyclerView.ViewHolder {
        TextView recipeTitle, recipeDescription;
        ImageView recipeImage;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeTitle = itemView.findViewById(R.id.recipeTitle);
            recipeDescription = itemView.findViewById(R.id.recipeDescription);
            recipeImage = itemView.findViewById(R.id.recipeImage);
        }
    }
}

