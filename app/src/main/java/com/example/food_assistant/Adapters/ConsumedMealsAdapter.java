package com.example.food_assistant.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.food_assistant.Models.MealIdentifier;
import com.example.food_assistant.Models.ProductIdentifier;
import com.example.food_assistant.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConsumedMealsAdapter extends RecyclerView.Adapter<ConsumedMealsAdapter.ViewHolder> {
    public interface ConsumedMealListener {
        void onPressFavoriteButton(int mealAdapterPosition);
        void onPressEditButton(int mealAdapterPosition);
    }

    private final ConsumedMealsAdapter.ConsumedMealListener consumedMealListener;
    private List<MealIdentifier> items;
    private boolean[] areFavorites;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageButton favoriteButton;
        private boolean isFavorite = false;

        public ViewHolder(View view, ConsumedMealsAdapter.ConsumedMealListener consumedProductListener) {
            super(view);
            textView = view.findViewById(R.id.textView_ingredient);
            favoriteButton = view.findViewById(R.id.imageButton_favorite);
            favoriteButton.setOnClickListener(v -> {
                isFavorite = !isFavorite;
                toggleFavoriteButton();
                consumedProductListener.onPressFavoriteButton(getAdapterPosition());
            });
            ImageButton editButton = view.findViewById(R.id.imageButton_edit);
            editButton.setOnClickListener(v -> consumedProductListener.onPressEditButton(getAdapterPosition()));
        }

        public void setMealIdentifier(MealIdentifier mealIdentifier, boolean isFavorite) {
            textView.setText(mealIdentifier.getMealName());
            this.isFavorite = isFavorite;
            toggleFavoriteButton();
        }

        private void toggleFavoriteButton() {
            if (isFavorite)
                favoriteButton.setImageResource(R.drawable.ic_favorite);
            else
                favoriteButton.setImageResource(R.drawable.ic_favorite_outline);
        }
    }

    public ConsumedMealsAdapter(List<MealIdentifier> items, boolean[] areFavorites, ConsumedMealsAdapter.ConsumedMealListener consumedMealListener) {
        this.items =  new ArrayList<>(items);
        this.areFavorites = areFavorites;
        this.consumedMealListener = consumedMealListener;
        System.out.println(items.toString());
    }

    @NotNull
    @Override
    public ConsumedMealsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_consumed_meal, viewGroup, false);

        ConsumedMealsAdapter.ViewHolder viewHolder = new ConsumedMealsAdapter.ViewHolder(view, consumedMealListener);
        view.setOnClickListener(v -> consumedMealListener.onPressEditButton(viewHolder.getAdapterPosition()));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ConsumedMealsAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.setMealIdentifier(items.get(position), areFavorites[position]);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public MealIdentifier itemAt(int position) {
        return items.get(position);
    }

    public boolean getIsFavorite(int position) {
        return areFavorites[position];
    }

    public void setIsFavorite(int position, boolean value) {
        areFavorites[position] = value;
    }

}
