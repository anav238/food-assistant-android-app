package com.example.food_assistant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        ExpandableListView elv = (ExpandableListView) findViewById(R.id.favoritesExpandableListView);

        HashMap<String, List<String>> expandableListDetail = new HashMap<String, List<String>>();
        List<String> cricket = new ArrayList<String>();
        cricket.add("India");
        List<String> football = new ArrayList<String>();
        football.add("Brazil");
        List<String> basketball = new ArrayList<String>();
        basketball.add("United States");
        expandableListDetail.put("Gnocchi", cricket);
        expandableListDetail.put("Tomato sauce", football);
        expandableListDetail.put("Coca Cola Zero", basketball);

        elv.setAdapter(new FavoritesExpandableListAdapter(getApplicationContext(), new ArrayList<>(expandableListDetail.keySet()), expandableListDetail));
    }

}