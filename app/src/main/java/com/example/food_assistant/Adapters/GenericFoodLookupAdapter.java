package com.example.food_assistant.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.food_assistant.Fragments.SelectProductQuantityFragment;
import com.example.food_assistant.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.food_assistant.Models.Product;
import java.util.List;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.ViewModels.ProductSharedViewModel;

public class GenericFoodLookupAdapter extends RecyclerView.Adapter<GenericFoodLookupAdapter.ViewHolder> {

    private List<Product> localDataSet;
    private final AppCompatActivity activity;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private Product product;
        private ProductSharedViewModel productSharedViewModel;

        public ViewHolder(View view, AppCompatActivity activity) {
            super(view);
            productSharedViewModel = new ViewModelProvider(activity).get(ProductSharedViewModel.class);

            textView = view.findViewById(R.id.textView);
            textView.setOnClickListener(v -> {
                productSharedViewModel.select(product);
                SelectProductQuantityFragment selectProductQuantityFragment = new SelectProductQuantityFragment();
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                selectProductQuantityFragment.show(fragmentManager, "test");
            });
        }

        public TextView getTextView() {
            return textView;
        }

        public void setProduct(Product product) {
            this.product = product;
        }
    }

    public GenericFoodLookupAdapter(List<Product> dataSet, AppCompatActivity activity) {
        localDataSet = dataSet;
        this.activity = activity;
        setHasStableIds(true);
    }

    public void setLocalDataSet(List<Product> localDataSet) {
        this.localDataSet = localDataSet;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.food_lookup_list_item, viewGroup, false);

        return new ViewHolder(view, activity);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.setIsRecyclable(false);
        viewHolder.getTextView().setText(localDataSet.get(position).getProductName());
        viewHolder.setProduct(localDataSet.get(position));
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
