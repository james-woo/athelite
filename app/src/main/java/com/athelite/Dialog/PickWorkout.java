package com.athelite.Dialog;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.athelite.Database.DBHandler;
import com.athelite.Model.WorkoutPlan;
import com.athelite.R;
import com.athelite.Tabs.CalendarTabFragment;
import com.athelite.Tabs.HomeTabFragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PickWorkout extends DialogFragment{

    private ListView _pickWorkoutList;
    private String[] _workouts;
    private ArrayList<WorkoutPlan> _workoutPlans = new ArrayList<>();
    private DBHandler _db;
    private long _dateTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_pick_workout, container, false);
        getDialog().setTitle("Select A Template");

        _pickWorkoutList = (ListView)rootView.findViewById(R.id.pick_workout_list);
        _db = new DBHandler(getActivity());
        _dateTime = getArguments().getLong("PickWorkout.dateTime");
        initWorkoutPlans();
        return rootView;
    }

    void initWorkoutPlans() {
        List<String> workouts = new ArrayList<>();
        _workoutPlans = _db.getWorkoutPlans();
        for(WorkoutPlan w : _workoutPlans) {
            workouts.add(w.getWorkoutPlanName());
        }
        _workouts = workouts.toArray(new String[0]);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, _workouts);

        _pickWorkoutList.setAdapter(adapter);
        _pickWorkoutList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                _db.createWorkoutPlanForDateTime(_workoutPlans.get(position), _dateTime);
                try {
                    HomeTabFragment.getInstance().updateHomePage();
                    CalendarTabFragment.getInstance().setSelectedDate(new Date(_dateTime));
                } catch (Exception e) {
                    ErrorDialog.logError("Error Updating Workout", e.getMessage());
                }
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

}
