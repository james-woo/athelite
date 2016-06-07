package com.athelite.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.athelite.Model.ExerciseSet;
import com.athelite.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ExerciseSetListAdapter extends ArrayAdapter<ExerciseSet> {
    private ArrayList<ExerciseSet> _exerciseSetList;
    private Context _context;
    private SharedPreferences _sp;

    public ExerciseSetListAdapter(Context context, ArrayList<ExerciseSet> exerciseSets) {
        super(context, 0, exerciseSets);
        this._exerciseSetList = exerciseSets;
        this._context = context;
        this._sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_view_exercise_set, parent, false);
        }

        TextView setNumber = (TextView) convertView.findViewById(R.id.view_exercise_set_number);
        EditText setWeight = (EditText) convertView.findViewById(R.id.view_exercise_set_weight);
        TextView setWeightType = (TextView) convertView.findViewById(R.id.view_exercise_set_weight_type);
        EditText setReps = (EditText) convertView.findViewById(R.id.view_exercise_set_reps);

        setNumber.setText(String.valueOf(_exerciseSetList.get(position).getSetNumber()));

        DecimalFormat weightDF = new DecimalFormat("######.##");
        double sWeight = _exerciseSetList.get(position).getSetWeight();
        String weight = String.valueOf(weightDF.format(sWeight));

        if(_sp.getString("units_setup", "lb").equals("lb") && (_sp.getString("units", "lb")).equals("kg") && _sp.getBoolean("switched_units", false)) {
            weight = String.valueOf(weightDF.format(_exerciseSetList.get(position).getSetWeight() / 2.2));
            _exerciseSetList.get(position).setWeightType("kg");
        } else if (_sp.getString("units_setup", "lb").equals("kg") && (_sp.getString("units", "lb")).equals("lb") && _sp.getBoolean("switched_units", false)) {
            weight = String.valueOf(weightDF.format(_exerciseSetList.get(position).getSetWeight() * 2.2));
            _exerciseSetList.get(position).setWeightType("lb");
        }

        setNumber.setText(String.valueOf(_exerciseSetList.get(position).getSetNumber()));
        setWeight.setText(weight);
        setWeightType.setText(_sp.getString("units", "lb"));
        setReps.setText(String.valueOf(_exerciseSetList.get(position).getSetReps()));

        SharedPreferences.Editor editor = _sp.edit();
        editor.putBoolean("switched_units", false);
        editor.apply();

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
