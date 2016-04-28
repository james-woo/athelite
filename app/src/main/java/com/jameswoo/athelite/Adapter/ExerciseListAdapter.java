package com.jameswoo.athelite.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jameswoo.athelite.Activity.ViewExercise;
import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.Model.ExerciseSet;
import com.jameswoo.athelite.R;
import com.jameswoo.athelite.Util.JsonSerializer;

import java.util.ArrayList;

public class ExerciseListAdapter extends ArrayAdapter<Exercise> {

    private Context _context;
    private ArrayList<Exercise> _exerciseList;

    public final static String WORKOUT_EXERCISE = "com.jameswoo.athelite.WORKOUT_EXERCISE";

    public ExerciseListAdapter(Context context, ArrayList<Exercise> exercises) {
        super(context, 0, exercises);
        this._context = context;
        this._exerciseList = exercises;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_view_workout_exercise, parent, false);
        }

        TextView workoutPlanExerciseName = (TextView) convertView.findViewById(R.id.workout_plan_exercise_name);
        TextView workoutPlanExerciseSets = (TextView) convertView.findViewById(R.id.workout_plan_exercise_sets);
        CardView eCardView = (CardView) convertView.findViewById(R.id.exercise_card_view);

        ArrayList<ExerciseSet> exerciseSetList = _exerciseList.get(position).getExerciseSets();
        final Exercise exercise = _exerciseList.get(position);

        StringBuilder exerciseSets = new StringBuilder();
        double setWidth = 20 * 0.3 * -1;
        double weightWidth = 20 * 0.3;
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

        workoutPlanExerciseName.setText(_exerciseList.get(position).getExerciseName());
        workoutPlanExerciseSets.setText(exerciseSets);

        eCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, ViewExercise.class);
                intent.putExtra(WORKOUT_EXERCISE, JsonSerializer.workoutPlanExerciseToJson(exercise));
                _context.startActivity(intent);
            }
        });

        return convertView;
    }

    public void updateExerciseList(ArrayList<Exercise> exercises) {
        _exerciseList = exercises;
    }
}