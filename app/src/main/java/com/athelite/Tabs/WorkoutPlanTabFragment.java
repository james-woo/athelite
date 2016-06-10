package com.athelite.Tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.athelite.Database.DBHandler;
import com.athelite.Dialog.ErrorDialog;
import com.athelite.R;
import com.athelite.Model.WorkoutPlan;
import com.athelite.Adapter.WorkoutPlanAdapter;

import java.util.ArrayList;

public class WorkoutPlanTabFragment extends Fragment {
    private RecyclerView _workoutPlanRecyclerView;
    private WorkoutPlanAdapter _workoutPlanAdapter;
    private DBHandler _db;
    private ArrayList<WorkoutPlan> _workoutPlans;
    //private ImageView _emptyList;
    private TextView _emptyList;

    private static WorkoutPlanTabFragment _wFragment = new WorkoutPlanTabFragment();
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    public WorkoutPlanTabFragment() {

    }

    public static WorkoutPlanTabFragment getInstance() {
        return _wFragment;
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static WorkoutPlanTabFragment newInstance(int sectionNumber) {
        WorkoutPlanTabFragment fragment = _wFragment;
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workout, container, false);
        _db = new DBHandler(getContext());
        _workoutPlans = _db.getWorkoutPlans();
        _workoutPlanRecyclerView = (RecyclerView) rootView.findViewById(R.id.workout_recycler_view);
        RecyclerView.LayoutManager _workoutPlanLayoutManager = new LinearLayoutManager(getContext());
        _workoutPlanRecyclerView.setLayoutManager(_workoutPlanLayoutManager);
        _workoutPlanAdapter = new WorkoutPlanAdapter(getContext(), _workoutPlans);
        _workoutPlanRecyclerView.setAdapter(_workoutPlanAdapter);
        _emptyList = (TextView) rootView.findViewById(R.id.workout_tab_empty_list_text);
        checkEmptyList();
        return rootView;
    }

    public void updateWorkoutPlanAdapter() {
        try {
            _workoutPlans.clear();
            _workoutPlans = _db.getWorkoutPlans();
            if (!_workoutPlans.isEmpty() && _workoutPlans != null) {
                _workoutPlanAdapter.updateWorkoutPlans(_workoutPlans);
                _workoutPlanAdapter.notifyDataSetChanged();
            }
        } catch(Exception e) {
            ErrorDialog.messageBox("Error Updating Workout", e.getMessage(), getContext());
        }
        checkEmptyList();
    }

    public void createNewWorkout() {
        try {
            _workoutPlanAdapter.addWorkoutPlan(_db.createWorkoutPlan());
            _workoutPlanAdapter.notifyDataSetChanged();
            checkEmptyList();
            _workoutPlanRecyclerView.smoothScrollToPosition(_workoutPlanAdapter.getItemCount());
        } catch(Exception e) {
            ErrorDialog.messageBox("Error Updating Workout", e.getMessage(), getContext());
        }
    }

    public void deleteWorkout(WorkoutPlan workoutPlan) {
        try {
            _workoutPlanAdapter.removeWorkoutPlan(workoutPlan);
            _workoutPlanAdapter.notifyItemRangeRemoved(0, _workoutPlanAdapter.getItemCount());
            checkEmptyList();
        } catch(Exception e) {
            ErrorDialog.messageBox("Error Updating Workout", e.getMessage(), getContext());
        }
    }

    public void checkEmptyList() {
        try {
            if (_workoutPlanAdapter.getItemCount() < 1) {
                _emptyList.setVisibility(View.VISIBLE);
            } else {
                _emptyList.setVisibility(View.INVISIBLE);
            }
        } catch(Exception e) {
            ErrorDialog.messageBox("Error Updating Workout", e.getMessage(), getContext());
        }
    }

}
