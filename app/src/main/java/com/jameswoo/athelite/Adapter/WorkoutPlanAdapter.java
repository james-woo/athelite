package com.jameswoo.athelite.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.jameswoo.athelite.Activity.MainActivity;
import com.jameswoo.athelite.Activity.ViewWorkout;
import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.Model.WorkoutPlan;
import com.jameswoo.athelite.R;
import com.jameswoo.athelite.Tabs.WorkoutPlanTabFragment;
import com.jameswoo.athelite.Util.JsonSerializer;

import java.util.ArrayList;

public class WorkoutPlanAdapter extends RecyclerView.Adapter<WorkoutPlanAdapter.ViewHolder> {

    private ArrayList<WorkoutPlan> _workOutPlanList;
    private Context _context;
    private DBHandler _db;

    public final static String WORKOUT_PLAN = "com.jameswoo.athelite.WORKOUT_PLAN";

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView vWorkoutPlanExerciseNames;
        public CardView vWorkoutCardView;
        public Toolbar vWorkoutCardMenu;

        public ViewHolder(View v) {
            super(v);
            vWorkoutPlanExerciseNames = (TextView) v.findViewById(R.id.workout_plan_exercise_names);
            vWorkoutCardView = (CardView) v.findViewById(R.id.card_view);
            vWorkoutCardMenu = (Toolbar) v.findViewById(R.id.card_workout_toolbar);
            vWorkoutCardMenu.inflateMenu(R.menu.menu_workout_card);
        }
    }

    public WorkoutPlanAdapter(Context context, ArrayList<WorkoutPlan> workouts) {
        _context = context;
        _workOutPlanList = workouts;
        _db = new DBHandler(_context);
    }

    @Override
    public WorkoutPlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_workout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.vWorkoutCardMenu.setTitle(_workOutPlanList.get(position).getWorkoutPlanName());
        if (holder.vWorkoutCardMenu != null) {
            holder.vWorkoutCardMenu.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    _db.deleteWorkoutPlan(_workOutPlanList.get(position));
                    _workOutPlanList.remove(position);
                    
                    notifyDataSetChanged();
                    return true;
                }
            });
        }

        holder.vWorkoutCardMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startViewWorkout(position);
            }
        });

        ArrayList<Exercise> workoutPlanExercises = _workOutPlanList.get(position).getWorkoutPlanExercises();
        String exerciseNames = "";

        if(workoutPlanExercises != null)
            for(Exercise e : workoutPlanExercises) {
                exerciseNames = exerciseNames + e.getExerciseName() + "\n";
            }
        holder.vWorkoutPlanExerciseNames.setText(exerciseNames);

        holder.vWorkoutCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startViewWorkout(position);
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
        notifyDataSetChanged();
    }

    public void updateWorkoutPlans(ArrayList<WorkoutPlan> workoutplans) {
        _workOutPlanList.clear();
        _workOutPlanList.addAll(workoutplans);
        notifyDataSetChanged();
    }

    private void startViewWorkout(int position) {
        Intent intent = new Intent(_context, ViewWorkout.class);
        intent.putExtra("VIEW_WORKOUT_PARENT", "Templates");
        intent.putExtra(WORKOUT_PLAN, JsonSerializer.workoutPlanToJson(_workOutPlanList.get(position)));
        _context.startActivity(intent);
    }
}
