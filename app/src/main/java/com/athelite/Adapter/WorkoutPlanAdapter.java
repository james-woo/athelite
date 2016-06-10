package com.athelite.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.athelite.Activity.ViewWorkout;
import com.athelite.Database.DBHandler;
import com.athelite.Model.Exercise;
import com.athelite.Model.WorkoutPlan;
import com.athelite.R;
import com.athelite.Tabs.WorkoutPlanTabFragment;
import com.athelite.Util.JsonSerializer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

public class WorkoutPlanAdapter extends RecyclerView.Adapter<WorkoutPlanAdapter.ViewHolder> {

    private ArrayList<WorkoutPlan> _workOutPlanList;
    private Context _context;
    private DBHandler _db;
    private SharedPreferences _sp;

    public final static String WORKOUT_PLAN = "com.athelite.WORKOUT_PLAN";

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView vWorkoutPlanExerciseNames;
        public CardView vWorkoutCardView;
        public Toolbar vWorkoutCardMenu;
        public TextView vWorkoutPlanExerciseOneReps;

        public ViewHolder(View v) {
            super(v);
            vWorkoutPlanExerciseNames = (TextView) v.findViewById(R.id.card_workout_plan_exercise_names);
            vWorkoutCardView = (CardView) v.findViewById(R.id.card_view);
            vWorkoutCardMenu = (Toolbar) v.findViewById(R.id.card_workout_toolbar);
            vWorkoutCardMenu.inflateMenu(R.menu.menu_workout_card);
            vWorkoutPlanExerciseOneReps = (TextView) v.findViewById(R.id.card_workout_plan_exercise_one_rep_max);
        }
    }

    public WorkoutPlanAdapter(Context context, ArrayList<WorkoutPlan> workouts) {
        _context = context;
        _workOutPlanList = workouts;
        _db = new DBHandler(_context);
        _sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public WorkoutPlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_workout, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.vWorkoutCardMenu.setTitle(_workOutPlanList.get(position).getWorkoutPlanName());
        if (holder.vWorkoutCardMenu != null) {
            holder.vWorkoutCardMenu.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch(menuItem.getItemId()) {
                        case R.id.action_move_workout_up:
                            if(_workOutPlanList.size() < 1 || position < 1) break;
                            long id_up = _workOutPlanList.get(position).getId();
                            _workOutPlanList.get(position).setId(id_up - 1);
                            _workOutPlanList.get(position - 1).setId(id_up);
                            _db.updateWorkoutPlan(_workOutPlanList.get(position));
                            _db.updateWorkoutPlan(_workOutPlanList.get(position - 1));
                            Collections.swap(_workOutPlanList, position, position - 1);
                            notifyItemRangeChanged(0, getItemCount());
                            break;
                        case R.id.action_move_workout_down:
                            if(_workOutPlanList.size() < 1 || position >= _workOutPlanList.size() - 1) break;
                            long id_down = _workOutPlanList.get(position).getId();
                            _workOutPlanList.get(position).setId(id_down + 1);
                            _workOutPlanList.get(position + 1).setId(id_down);
                            _db.updateWorkoutPlan(_workOutPlanList.get(position));
                            _db.updateWorkoutPlan(_workOutPlanList.get(position + 1));
                            Collections.swap(_workOutPlanList, position, position + 1);
                            notifyItemRangeChanged(0, getItemCount());
                            break;
                        case R.id.action_delete_workout:
                            _db.deleteWorkoutPlan(_workOutPlanList.get(position));
                            _workOutPlanList.remove(position);
                            WorkoutPlanTabFragment.getInstance().checkEmptyList();
                            notifyDataSetChanged();
                            break;
                    }
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
        String exerciseOneReps = "";
        DecimalFormat oneRepDF = new DecimalFormat("#####.##");

        if(!workoutPlanExercises.isEmpty()) {
            for (Exercise e : workoutPlanExercises) {
                if (e == null) break;
                exerciseNames = exerciseNames + e.getExerciseName() + "\n";
                exerciseOneReps = exerciseOneReps + " 1 RM: " + oneRepDF.format(e.getOneRepMax()) + " " + _sp.getString("units", "lb") + "\n";
            }
        }
        holder.vWorkoutPlanExerciseNames.setText(exerciseNames);
        holder.vWorkoutPlanExerciseOneReps.setText(exerciseOneReps);

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

    public void removeWorkoutPlan(WorkoutPlan workoutPlan) {
        _workOutPlanList.remove(workoutPlan);
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
