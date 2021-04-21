package com.example.food_assistant.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.fragment.app.Fragment;

import com.example.food_assistant.Adapters.HistoryExpandableListAdapter;
import com.example.food_assistant.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HistoryFragment extends Fragment {

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history, null);
        ExpandableListView elv = (ExpandableListView) v.findViewById(R.id.expandableListView);

        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
        List<String> cricket = new ArrayList<String>();
        cricket.add("India");
        List<String> football = new ArrayList<String>();
        football.add("Brazil");
        List<String> basketball = new ArrayList<String>();
        basketball.add("United States");
        expandableListDetail.put("Yesterday", cricket);
        expandableListDetail.put("Sunday", football);
        expandableListDetail.put("Saturday", basketball);

        elv.setAdapter(new HistoryExpandableListAdapter(getContext(), new ArrayList<>(expandableListDetail.keySet()), expandableListDetail));
        return v;
    }

}