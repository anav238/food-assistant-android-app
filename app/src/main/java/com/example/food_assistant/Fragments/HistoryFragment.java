package com.example.food_assistant.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.food_assistant.Adapters.NutritionHistoryExpandableListAdapter;
import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.R;
import com.example.food_assistant.Utils.ViewModels.UserSharedViewModel;

import java.util.ArrayList;
import java.util.Map;

public class HistoryFragment extends Fragment {

    private UserSharedViewModel userSharedViewModel;
    private int lastExpandedPosition = -1;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userSharedViewModel = new ViewModelProvider(requireActivity()).get(UserSharedViewModel.class);
        userSharedViewModel.getSelected().observe(this,  provider -> updateNutrientHistory(getView()));
    }

    private void updateNutrientHistory(View v) {
        ExpandableListView elv = v.findViewById(R.id.expandableListView);

        elv.setOnGroupExpandListener(groupPosition -> {
            if (lastExpandedPosition != -1
                    && groupPosition != lastExpandedPosition) {
                elv.collapseGroup(lastExpandedPosition);
            }
            lastExpandedPosition = groupPosition;
        });

        AppUser user = userSharedViewModel.getSelected().getValue();
        Map<String, Map<String, Double>> nutrientConsumptionHistory = user.getNutrientConsumptionHistory();

        elv.setAdapter(new NutritionHistoryExpandableListAdapter(requireActivity(), getContext(), user, new ArrayList<>(nutrientConsumptionHistory.keySet()), nutrientConsumptionHistory));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, null);
        updateNutrientHistory(v);
        return v;
    }

}