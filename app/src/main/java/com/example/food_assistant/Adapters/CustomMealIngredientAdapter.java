package com.example.food_assistant.Adapters;

import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_assistant.Models.Product;
import com.example.food_assistant.R;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CustomMealIngredientAdapter extends RecyclerView.Adapter<CustomMealIngredientAdapter.ViewHolder> {

    public interface MealIngredientListener {
        void onPressEditButton(int productAdapterPosition);
        void onPressRemoveButton(int productAdapterPosition);
    }

    private final MealIngredientListener mealIngredientListener;
    private List<Product> items;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private Product product;

        public ViewHolder(View view, MealIngredientListener mealIngredientListener) {
            super(view);
            textView = view.findViewById(R.id.textView_ingredient);
            ImageButton editButton = view.findViewById(R.id.imageButton_edit);
            editButton.setOnClickListener(v -> mealIngredientListener.onPressEditButton(getAdapterPosition()));
            ImageButton removeButton = view.findViewById(R.id.imageButton_remove);
            removeButton.setOnClickListener(v -> mealIngredientListener.onPressRemoveButton(getAdapterPosition()));
        }

        public void setProduct(Product product) {
            this.product = product;
            updateTextView();
        }

        private void updateTextView() {
            textView.setText(product.getProductName() + " - " + product.getBaseQuantity() + product.getMeasurementUnit());
        }

    }

    public CustomMealIngredientAdapter(List<Product> dataSet, MealIngredientListener mealIngredientListener) {
        items = dataSet;
        this.mealIngredientListener = mealIngredientListener;
    }


    @NotNull
    @Override
    public CustomMealIngredientAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_custom_meal_ingredient, viewGroup, false);

        return new CustomMealIngredientAdapter.ViewHolder(view, mealIngredientListener);
    }

    @Override
    public void onBindViewHolder(CustomMealIngredientAdapter.ViewHolder viewHolder, final int position) {
        //viewHolder.setIsRecyclable(false);
        viewHolder.setProduct(items.get(position));
    }

    public List<Product> getItems() {
        return items;
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

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, 1);
    }

    public Product itemAt(int position) {
        return items.get(position);
    }

    public void editItemQuantity(int itemPosition, double newQuantity) {
        items.get(itemPosition).setBaseQuantity(newQuantity);
        notifyItemChanged(itemPosition);
    }
}
