package com.example.food_assistant.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_assistant.Models.Product;
import com.example.food_assistant.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomMealIngredientAdapter extends RecyclerView.Adapter<CustomMealIngredientAdapter.ViewHolder> {
    private List<Product> items;
    private final AppCompatActivity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private Product product;

        public ViewHolder(View view, AppCompatActivity activity) {
            super(view);
            textView = view.findViewById(R.id.textView_ingredient);
        }

        public TextView getTextView() {
            return textView;
        }

        public void setProduct(Product product) {
            this.product = product;
        }
    }

    public CustomMealIngredientAdapter(List<Product> dataSet, AppCompatActivity activity) {
        items = dataSet;
        this.activity = activity;
        setHasStableIds(true);
    }

    public void setItems(List<Product> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @NotNull
    @Override
    public CustomMealIngredientAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_custom_meal_ingredient, viewGroup, false);

        return new CustomMealIngredientAdapter.ViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(CustomMealIngredientAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.setIsRecyclable(false);
        viewHolder.getTextView().setText(items.get(position).getProductName());
        viewHolder.setProduct(items.get(position));
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

    public void addItem(Product item) {
        items.add(item);
        notifyDataSetChanged();
    }
}
