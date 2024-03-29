package com.example.food_assistant.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.food_assistant.Models.ProductIdentifier;
import com.example.food_assistant.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConsumedProductsAdapter extends RecyclerView.Adapter<ConsumedProductsAdapter.ViewHolder>  {

    public interface ConsumedProductListener {
        void onPressFavoriteButton(int productAdapterPosition);
        void onPressInfoButton(int productAdapterPosition);
    }

    private final ConsumedProductsAdapter.ConsumedProductListener consumedProductListener;
    private List<ProductIdentifier> items;
    private boolean[] areFavorites;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final ImageButton favoriteButton;
        private boolean isFavorite = false;

        public ViewHolder(View view, ConsumedProductsAdapter.ConsumedProductListener consumedProductListener) {
            super(view);
            textView = view.findViewById(R.id.textView_ingredient);
            favoriteButton = view.findViewById(R.id.imageButton_favorite);
            favoriteButton.setOnClickListener(v -> {
                isFavorite = !isFavorite;
                toggleFavoriteButton();
                consumedProductListener.onPressFavoriteButton(getAdapterPosition());
            });
            ImageButton infoButton = view.findViewById(R.id.imageButton_edit);
            infoButton.setOnClickListener(v -> consumedProductListener.onPressInfoButton(getAdapterPosition()));
        }

        public void setProductIdentifier(ProductIdentifier productIdentifier, boolean isFavorite) {
            textView.setText(productIdentifier.getProductName());
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

    public ConsumedProductsAdapter(List<ProductIdentifier> items, boolean[] areFavorites, ConsumedProductsAdapter.ConsumedProductListener consumedProductListener) {
        this.items =  new ArrayList<>(items);
        this.areFavorites = areFavorites;
        this.consumedProductListener = consumedProductListener;
        System.out.println(items.toString());
    }

    @NotNull
    @Override
    public ConsumedProductsAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_consumed_product, viewGroup, false);

        ConsumedProductsAdapter.ViewHolder viewHolder = new ConsumedProductsAdapter.ViewHolder(view, consumedProductListener);
        view.setOnClickListener(v -> consumedProductListener.onPressInfoButton(viewHolder.getAdapterPosition()));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ConsumedProductsAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.setProductIdentifier(items.get(position), areFavorites[position]);
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

    public ProductIdentifier itemAt(int position) {
        System.out.println(items);
        return items.get(position);
    }

    public boolean getIsFavorite(int position) {
        return areFavorites[position];
    }

    public void setIsFavorite(int position, boolean value) {
        areFavorites[position] = value;
    }

}
