package com.athelite.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.athelite.Activity.ViewExercise;
import com.athelite.Database.DBHandler;
import com.athelite.Model.Exercise;
import com.athelite.Model.ExerciseSet;
import com.athelite.Model.WorkoutPlan;
import com.athelite.R;
import com.athelite.Util.JsonSerializer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class ExerciseListAdapter extends ArrayAdapter<Exercise> {

    private Context _context;
    private ArrayList<Exercise> _exerciseList;
    private Toolbar _exerciseToolbar;
    private DBHandler _db;
    private WorkoutPlan _workoutPlan;
    private SharedPreferences _sp;

    public final static String WORKOUT_EXERCISE = "com.athelite.WORKOUT_EXERCISE";

    public ExerciseListAdapter(Context context, ArrayList<Exercise> exercises, WorkoutPlan workoutPlan) {
        super(context, 0, exercises);
        this._context = context;
        this._exerciseList = exercises;
        this._db = new DBHandler(_context);
        this._workoutPlan = workoutPlan;
        this._sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.card_workout_exercise, parent, false);
            _exerciseToolbar = (Toolbar) convertView.findViewById(R.id.card_exercise_toolbar);
            _exerciseToolbar.inflateMenu(R.menu.menu_exercise_card);
        }

        ((Toolbar)convertView.findViewById(R.id.card_exercise_toolbar)).setTitle(_exerciseList.get(position).getExerciseName());
        ((Toolbar)convertView.findViewById(R.id.card_exercise_toolbar)).setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.action_move_exercise_up:
                        if(_exerciseList.size() < 1 || position < 1) break;
                        long id_up = _exerciseList.get(position).getId();
                        _exerciseList.get(position).setId(id_up - 1);
                        _exerciseList.get(position - 1).setId(id_up);
                        _db.updateExercise(_exerciseList.get(position));
                        _db.updateExercise(_exerciseList.get(position - 1));
                        Collections.swap(_exerciseList, position, position - 1);
                        notifyDataSetChanged();
                        break;
                    case R.id.action_move_exercise_down:
                        if(_exerciseList.size() < 1 || position >= _exerciseList.size() - 1) break;
                        long id_down = _exerciseList.get(position).getId();
                        _exerciseList.get(position).setId(id_down + 1);
                        _exerciseList.get(position + 1).setId(id_down);
                        _db.updateExercise(_exerciseList.get(position));
                        _db.updateExercise(_exerciseList.get(position + 1));
                        Collections.swap(_exerciseList, position, position + 1);
                        notifyDataSetChanged();
                        break;
                    case R.id.action_delete_exercise:
                        _db.deleteExercise(_exerciseList.get(position));
                        _exerciseList.remove(position);
                        Toast.makeText(getContext(), "Exercise Deleted", Toast.LENGTH_SHORT).show();
                        if(_exerciseList.isEmpty()) {
                            Toast.makeText(getContext(), "Exercise Deleted, Default Exercise Added", Toast.LENGTH_SHORT).show();
                            _exerciseList.add(_db.createExerciseForWorkoutPlanId(_db.getWritableDatabase(), _workoutPlan.getId()));
                        }

                        notifyDataSetChanged();
                        break;
                }
                return true;
            }
        });

        double oneRepMax = _exerciseList.get(position).getOneRepMax();
        DecimalFormat oneRepDF = new DecimalFormat("#####.##");
        TextView oneRepMaxTV = (TextView) convertView.findViewById(R.id.card_exercise_one_rep_max);
        oneRepMaxTV.setText(String.format("%s %s %s","1 RM: ", String.valueOf(oneRepDF.format(oneRepMax)), _sp.getString("units", "lb")));

        TextView exerciseSetNumberTV = (TextView) convertView.findViewById(R.id.card_exercise_set_number);
        TextView exerciseSetWeightTV = (TextView) convertView.findViewById(R.id.card_exercise_set_weight);
        TextView exerciseSetRepsTV = (TextView) convertView.findViewById(R.id.card_exercise_set_reps);
        CardView eCardView = (CardView) convertView.findViewById(R.id.exercise_card_view);

        ArrayList<ExerciseSet> exerciseSetList = _exerciseList.get(position).getExerciseSets();

        String exerciseSetNumber = "";
        String exerciseSetWeight = "";
        String exerciseSetReps = "";

        for(ExerciseSet es : exerciseSetList) {
            DecimalFormat weightDF = new DecimalFormat("######.##");
            DecimalFormat repDF = new DecimalFormat("###### reps");
            DecimalFormat setDF = new DecimalFormat("Set ###");

            String weight = String.valueOf(weightDF.format(es.getSetWeight()));
            es.setWeightType(_sp.getString("units", "lb"));

            if(_sp.getString("units_setup", "lb").equals("lb") && (_sp.getString("units", "lb")).equals("kg") && _sp.getBoolean("switched_units", false)) {
                weight = String.valueOf(weightDF.format(es.getSetWeight() / 2.2));
                es.setWeightType("kg");
            } else if (_sp.getString("units_setup", "lb").equals("kg") && (_sp.getString("units", "lb")).equals("lb") && _sp.getBoolean("switched_units", false)) {
                weight = String.valueOf(weightDF.format(es.getSetWeight() * 2.2));
                es.setWeightType("lb");
            }

            String weightType = es.getWeightType();

            exerciseSetNumber += String.format(Locale.US, "%s\n",
                    String.valueOf(setDF.format(es.getSetNumber())));

            exerciseSetWeight += String.format(Locale.US, "%s %s\n", weight, weightType);

            exerciseSetReps += String.format(Locale.US, "%s\n",
                    String.valueOf(repDF.format(es.getSetReps())));

        }
        SharedPreferences.Editor editor = _sp.edit();
        editor.putBoolean("switched_units", false);
        editor.apply();

        exerciseSetNumberTV.setText(exerciseSetNumber);
        exerciseSetWeightTV.setText(exerciseSetWeight);
        exerciseSetRepsTV.setText(exerciseSetReps);

        ((Toolbar)convertView.findViewById(R.id.card_exercise_toolbar)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startViewExercise(position);
            }
        });

        eCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startViewExercise(position);
            }
        });

        return convertView;
    }

    public void updateExerciseList(ArrayList<Exercise> exercises) {
        _exerciseList.clear();
        _exerciseList.addAll(exercises);
    }

    private void startViewExercise(int position) {
        Intent intent = new Intent(_context, ViewExercise.class);
        intent.putExtra(WORKOUT_EXERCISE, JsonSerializer.workoutPlanExerciseToJson(_exerciseList.get(position)));
        _context.startActivity(intent);
    }

    public void addExercise(Exercise exercise) {
        _exerciseList.add(exercise);
    }

    public ArrayList<Exercise> getExerciseList() {
        return _exerciseList;
    }

    public void setWorkout(WorkoutPlan workout) {
        _workoutPlan = workout;
    }
}