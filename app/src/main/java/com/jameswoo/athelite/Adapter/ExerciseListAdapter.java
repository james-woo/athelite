package com.jameswoo.athelite.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jameswoo.athelite.Activity.ViewExercise;
import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.Model.ExerciseSet;
import com.jameswoo.athelite.Model.WorkoutPlan;
import com.jameswoo.athelite.R;
import com.jameswoo.athelite.Util.JsonSerializer;

import java.util.ArrayList;

public class ExerciseListAdapter extends ArrayAdapter<Exercise> {

    private Context _context;
    private ArrayList<Exercise> _exerciseList;
    private Toolbar _exerciseToolbar;
    private DBHandler _db;
    private WorkoutPlan _workoutPlan;

    public final static String WORKOUT_EXERCISE = "com.jameswoo.athelite.WORKOUT_EXERCISE";

    public ExerciseListAdapter(Context context, ArrayList<Exercise> exercises, WorkoutPlan workoutPlan) {
        super(context, 0, exercises);
        this._context = context;
        this._exerciseList = exercises;
        this._db = new DBHandler(_context);
        this._workoutPlan = workoutPlan;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_view_workout_exercise, parent, false);
            _exerciseToolbar = (Toolbar) convertView.findViewById(R.id.card_exercise_toolbar);
            _exerciseToolbar.inflateMenu(R.menu.menu_exercise_card);
        }

        ((Toolbar)convertView.findViewById(R.id.card_exercise_toolbar)).setTitle(_exerciseList.get(position).getExerciseName());
        _exerciseToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                _db.deleteExercise(_exerciseList.get(position));
                _exerciseList.remove(position);
                if(_exerciseList.isEmpty()) {
                    _exerciseList.add(_db.createExerciseForWorkoutPlan(_db.getWritableDatabase(), _workoutPlan));
                }
                notifyDataSetChanged();
                return true;
            }
        });
        _exerciseToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startViewExercise(position);
            }
        });

        TextView workoutPlanExerciseSets = (TextView) convertView.findViewById(R.id.workout_plan_exercise_sets);
        CardView eCardView = (CardView) convertView.findViewById(R.id.exercise_card_view);

        ArrayList<ExerciseSet> exerciseSetList = _exerciseList.get(position).getExerciseSets();

        StringBuilder exerciseSets = new StringBuilder();
        int widthPixels = eCardView.getResources().getDisplayMetrics().widthPixels;
        double setWidth = widthPixels * 0.015 * -1;
        double weightWidth = widthPixels * 0.015;
        for(ExerciseSet es : exerciseSetList) {

            exerciseSets.append("Set ")
                        .append(es.getSetNumber())
                        .append(String.format("%" + setWidth + "s", " "))
                        .append(es.getSetWeight())
                        .append(" lb")
                        .append(String.format("%" + weightWidth + "s", " "))
                        .append(es.getSetReps())
                        .append(" reps\n");
        }

        workoutPlanExerciseSets.setText(exerciseSets);

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
}