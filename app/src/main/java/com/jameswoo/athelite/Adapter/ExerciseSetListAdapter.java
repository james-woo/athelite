package com.jameswoo.athelite.Adapter;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.jameswoo.athelite.Activity.ViewExercise;
import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.Model.ExerciseSet;
import com.jameswoo.athelite.R;

import java.util.ArrayList;

public class ExerciseSetListAdapter extends ArrayAdapter<ExerciseSet> {
    private ArrayList<ExerciseSet> _exerciseSetList;
    private Context _context;

    public ExerciseSetListAdapter(Context context, ArrayList<ExerciseSet> exerciseSets) {
        super(context, 0, exerciseSets);
        this._exerciseSetList = exerciseSets;
        this._context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_view_exercise_set, parent, false);
        }

        TextView setNumber = (TextView) convertView.findViewById(R.id.set_number);
        EditText setWeight = (EditText) convertView.findViewById(R.id.set_weight);
        EditText setReps = (EditText) convertView.findViewById(R.id.set_reps);

        //final ExerciseSet exerciseSet = _exerciseSetList.get(position);
        final EditText weight = setWeight;
        final EditText reps = setReps;

        setWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !(weight.getText().toString()).equals("")){
                    if(Double.compare(_exerciseSetList.get(position).getSetWeight(),
                            Double.parseDouble(weight.getText().toString())) != 0) {
                        _exerciseSetList.get(position).setSetWeight(Double.parseDouble(weight.getText().toString()));
                    }
                }
            }
        });

        setReps.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus && !(reps.getText().toString()).equals("")){
                    if(Double.compare(_exerciseSetList.get(position).getSetWeight(),
                            Double.parseDouble(reps.getText().toString())) != 0) {
                        _exerciseSetList.get(position).setSetReps(Integer.parseInt(reps.getText().toString()));
                    }
                }
            }
        });

        setNumber.setText(String.valueOf(_exerciseSetList.get(position).getSetNumber()));
        setWeight.setText(String.valueOf(_exerciseSetList.get(position).getSetWeight()));
        setReps.setText(String.valueOf(_exerciseSetList.get(position).getSetReps()));

        return convertView;
    }

    public ArrayList<ExerciseSet> getExerciseSets() {
        return _exerciseSetList;
    }

    public void updateExerciseSetList(ArrayList<ExerciseSet> exerciseSetList) {
        _exerciseSetList.clear();
        _exerciseSetList.addAll(exerciseSetList);
    }

    public void addExerciseSet(ExerciseSet es) {
        _exerciseSetList.add(es);
    }

    public void removeLastExerciseSet() {
        _exerciseSetList.remove(_exerciseSetList.size() - 1);
    }
}
