package com.example.food_assistant.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.example.food_assistant.Fragments.NutrientIntakeFragment;
import com.example.food_assistant.Models.AppUser;
import com.example.food_assistant.R;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HistoryExpandableListAdapter extends BaseExpandableListAdapter {

    private final FragmentActivity activity;
    private final Context context;
    private final AppUser user;
    private final List<String> expandableListTitle;
    private final Map<String, Map<String, Double>> expandableListDetail;

    public HistoryExpandableListAdapter(FragmentActivity activity, Context context, AppUser user, List<String> expandableListTitle,
                                        Map<String, Map<String, Double>> expandableListDetail) {
        this.activity = activity;
        this.context = context;
        this.user = user;
        this.expandableListTitle = expandableListTitle;
        Collections.sort(expandableListTitle, Collections.reverseOrder());
        this.expandableListDetail = expandableListDetail;
    }


    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition));
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        //if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_nutrient_intake, null);

            Map<String, Double> maxNutrientDVs = user.getMaximumNutrientDV();
            Map<String, Double> daysNutrientConsumption = expandableListDetail.get(expandableListTitle.get(listPosition));
            Bundle bundle = new Bundle();
            bundle.putStringArray("nutrients", maxNutrientDVs.keySet().toArray(new String[maxNutrientDVs.keySet().size()]));
            for (String nutrient:maxNutrientDVs.keySet()) {
                int nutrientPercentage = (int) (daysNutrientConsumption.get(nutrient) * 100 / maxNutrientDVs.get(nutrient));
                bundle.putInt(nutrient, nutrientPercentage);
            }

            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.beginTransaction()
                        .setReorderingAllowed(true)
                        .add(R.id.nutrient_intake_fragment, NutrientIntakeFragment.class, bundle)
                        .commit();

        //}
        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.expandableListTitle.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = convertView.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}