package com.jameswoo.athelite.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.jameswoo.athelite.Activity.ViewWorkout;
import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.Model.WorkoutPlan;
import com.jameswoo.athelite.R;
import com.jameswoo.athelite.Util.JsonSerializer;

import java.util.ArrayList;

public class WorkoutPlanAdapter extends RecyclerView.Adapter<WorkoutPlanAdapter.ViewHolder> {

    private ArrayList<WorkoutPlan> _workOutPlanList;
    private Context _context;

    public final static String WORKOUT_PLAN = "com.jameswoo.athelite.WORKOUT_PLAN";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder{
        // each data item is just a string in this case
        public TextView vWorkoutPlanName;
        public TextView vWorkoutPlanExerciseNames;
        public CardView vWorkoutCardView;

        public ViewHolder(View v) {
            super(v);
            vWorkoutPlanName = (TextView) v.findViewById(R.id.workout_plan_name);
            vWorkoutPlanExerciseNames = (TextView) v.findViewById(R.id.workout_plan_exercise_names);
            vWorkoutCardView = (CardView) v.findViewById(R.id.card_view);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public WorkoutPlanAdapter(Context context, ArrayList<WorkoutPlan> workouts) {
        _context = context;
        _workOutPlanList = workouts;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public WorkoutPlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_workout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String workoutPlanName = _workOutPlanList.get(position).getWorkoutPlanName();
        final WorkoutPlan workoutPlan = _workOutPlanList.get(position);
        ArrayList<Exercise> workoutPlanExercises = _workOutPlanList.get(position).getWorkoutPlanExercises();
        String exerciseNames = "";

        if(workoutPlanExercises != null)
            for(Exercise e : workoutPlanExercises) {
                exerciseNames = exerciseNames + e.getExerciseName() + "\n";
            }
        holder.vWorkoutPlanName.setText(workoutPlanName);
        holder.vWorkoutPlanExerciseNames.setText(exerciseNames);

        holder.vWorkoutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(_context, ViewWorkout.class);
                intent.putExtra(WORKOUT_PLAN, JsonSerializer.workoutPlanToJson(workoutPlan));
                _context.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return _workOutPlanList.size();
    }

    public void addWorkoutPlan(WorkoutPlan workoutPlan) {
        _workOutPlanList.add(workoutPlan);
    }

    public void updateWorkoutPlans(ArrayList<WorkoutPlan> workoutplans) {
        _workOutPlanList = workoutplans;
    }
}
