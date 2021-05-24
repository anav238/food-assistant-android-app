package com.example.food_assistant.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_assistant.Fragments.SelectProductQuantityFragment;
import com.example.food_assistant.Models.Product;
import com.example.food_assistant.Models.RecipeIngredient;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;

import java.util.List;

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.ViewHolder> {
    private List<RecipeIngredient> localDataSet;
    private final AppCompatActivity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private RecipeIngredient recipeIngredient;
        private EditText ingredientQuantityEditText;
        private Spinner measurementUnitSpinner;
        private AutoCompleteTextView ingredientNameTextView;
        private AppCompatActivity activity;

        public ViewHolder(View view, AppCompatActivity activity) {
            super(view);
            this.activity = activity;

            ingredientQuantityEditText = view.findViewById(R.id.editText_quantity);

            measurementUnitSpinner = view.findViewById(R.id.spinner_measurement_units);
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity,
                    R.array.measurement_units_recipes, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            measurementUnitSpinner.setAdapter(adapter);

            ingredientNameTextView = view.findViewById(R.id.autoCompleteTextView_ingredients);

        }

        public void setRecipeIngredient(RecipeIngredient recipeIngredient) {
            this.recipeIngredient = recipeIngredient;
            ingredientQuantityEditText.setText(recipeIngredient.getQuantity());
            measurementUnitSpinner.setSelection(0);
            ingredientNameTextView.setText(recipeIngredient.getIngredient().getProductName());
        }
    }

    public RecipeIngredientAdapter(List<RecipeIngredient> dataSet, AppCompatActivity activity) {
        localDataSet = dataSet;
        this.activity = activity;
        setHasStableIds(true);
    }

    public void setLocalDataSet(List<RecipeIngredient> localDataSet) {
        this.localDataSet = localDataSet;
        notifyDataSetChanged();
    }

    @Override
    public RecipeIngredientAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recipe_ingredient_list_item, viewGroup, false);

        return new RecipeIngredientAdapter.ViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(RecipeIngredientAdapter.ViewHolder viewHolder, final int position) {
        viewHolder.setIsRecyclable(false);
        viewHolder.setRecipeIngredient(localDataSet.get(position));
    }

    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
