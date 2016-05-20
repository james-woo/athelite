package com.jameswoo.athelite.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.util.ArrayMap;
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

        TextView setNumber = (TextView) convertView.findViewById(R.id.set_number);
        EditText setWeight = (EditText) convertView.findViewById(R.id.set_weight);
        TextView setWeightType = (TextView) convertView.findViewById(R.id.set_weight_type);
        EditText setReps = (EditText) convertView.findViewById(R.id.set_reps);

        setNumber.setText(String.valueOf(_exerciseSetList.get(position).getSetNumber()));

        DecimalFormat weightDF = new DecimalFormat("######.##");
        double sWeight = _exerciseSetList.get(position).getSetWeight();
        String weight = String.valueOf(weightDF.format(sWeight));

        setNumber.setText(String.valueOf(_exerciseSetList.get(position).getSetNumber()));
        setWeight.setText(weight);
        setWeightType.setText(_sp.getString("units", "lb"));
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
