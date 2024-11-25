package com.example.moodchef;

import android.content.Intent;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<RecipeResponse.Recipe> recipeList;

    public RecipeAdapter(List<RecipeResponse.Recipe> recipeList) {
        this.recipeList = recipeList;
    }

    public void updateRecipes(List<RecipeResponse.Recipe> newRecipes) {
        recipeList.clear();
        recipeList.addAll(newRecipes);
        notifyDataSetChanged();
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
        String description = recipe.getSourceUrl() != null ? recipe.getSourceUrl() : "";
        holder.recipeDescription.setText(description);

        Glide.with(holder.itemView.getContext())
                .load(recipe.getImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.placeholder_image)
                .into(holder.recipeImage);

        holder.itemView.setOnClickListener(v -> {
            int recipeId = recipe.getId();
            RecipeService recipeService = RetrofitInstance.getRecipeApi();

            recipeService.getRecipeInformation(recipeId, BuildConfig.SPOONACULAR_API_KEY)
                    .enqueue(new Callback<RecipeDetailResponse>() {
                        @Override
                        public void onResponse(Call<RecipeDetailResponse> call, Response<RecipeDetailResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                RecipeDetailResponse recipeDetail = response.body();

                                StringBuilder ingredientsBuilder = new StringBuilder();
                                for (RecipeDetailResponse.ExtendedIngredient ingredient : recipeDetail.getExtendedIngredients()) {
                                    ingredientsBuilder.append("- ").append(ingredient.getOriginal()).append("\n");
                                }

                                Intent intent = new Intent(holder.itemView.getContext(), RecipeDetailActivity.class);
                                intent.putExtra("imageUrl", recipeDetail.getImage());
                                intent.putExtra("title", recipeDetail.getTitle());
                                intent.putExtra("ingredients", ingredientsBuilder.toString());
                                intent.putExtra("instructions", recipeDetail.getInstructions() != null
                                        ? recipeDetail.getInstructions()
                                        : "Instructions not available");
                                holder.itemView.getContext().startActivity(intent);
                            } else {
                                Log.e("RecipeDetail", "Failed to fetch recipe details");
                            }
                        }

                        @Override
                        public void onFailure(Call<RecipeDetailResponse> call, Throwable t) {
                            Log.e("RecipeDetailError", t.getMessage());
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
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