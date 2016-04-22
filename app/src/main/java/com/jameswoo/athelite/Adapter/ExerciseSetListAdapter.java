package com.jameswoo.athelite.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jameswoo.athelite.Model.ExerciseSet;
import com.jameswoo.athelite.R;

import java.util.ArrayList;

public class ExerciseSetListAdapter extends ArrayAdapter<ExerciseSet> {
    private ArrayList<ExerciseSet> _exerciseSetList;

    public ExerciseSetListAdapter(Context context, ArrayList<ExerciseSet> exerciseSets) {
        super(context, 0, exerciseSets);
        this._exerciseSetList = exerciseSets;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_view_exercise_set, parent, false);
        }

        TextView setNumber = (TextView) convertView.findViewById(R.id.set_number);
        TextView setWeight = (TextView) convertView.findViewById(R.id.set_weight);
        TextView setReps = (TextView) convertView.findViewById(R.id.set_reps);

        setNumber.setText(String.valueOf(_exerciseSetList.get(position).getSetNumber()));
        setWeight.setText(String.valueOf(_exerciseSetList.get(position).getSetWeight()));
        setReps.setText(String.valueOf(_exerciseSetList.get(position).getSetReps()));

        return convertView;
    }
}
