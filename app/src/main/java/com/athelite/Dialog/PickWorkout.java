package com.athelite.Dialog;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.athelite.Util.AlarmReceiver;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    public void setNotification() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if(_dateTime > System.currentTimeMillis() && sp.getBoolean("receive_notifications", true)) {
            AlarmManager alarmMgr = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getActivity(), AlarmReceiver.class);
            intent.putExtra("VIEW_DAY_DATETIME", _dateTime);
            PendingIntent alarmIntent = PendingIntent.getBroadcast(getActivity(), (int)System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(_dateTime);
            cal.set(Calendar.HOUR, TimePreference.getHour(sp.getString("notification_time", "12:00")));
            cal.set(Calendar.MINUTE, TimePreference.getMinute(sp.getString("notification_time", "12:00")));
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), alarmIntent);
        }
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
                    setNotification();
                } catch (Exception e) {
                    ErrorDialog.logError("Error Updating Day", e.getMessage());
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
