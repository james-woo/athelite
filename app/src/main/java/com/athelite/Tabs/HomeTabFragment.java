package com.athelite.Tabs;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.athelite.Activity.ViewDay;
import com.athelite.Activity.ViewWorkout;
import com.athelite.Adapter.WorkoutPlanAdapter;
import com.athelite.Database.DBHandler;
import com.athelite.Model.WorkoutPlan;
import com.athelite.R;
import com.athelite.Util.JsonSerializer;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import br.com.goncalves.pugnotification.notification.PugNotification;

public class HomeTabFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";


    private DBHandler _db;
    private TextView _todayWorkoutTextView;
    private TextView _nextWorkoutTextView;
    private TextView _prevWorkoutTextView;

    private CardView _todayWorkoutCard;
    private CardView _nextWorkoutCard;
    private CardView _prevWorkoutCard;

    private WorkoutPlan _todayWorkout;
    private WorkoutPlan _nextWorkout;
    private WorkoutPlan _prevWorkout;

    private SharedPreferences _sp;

    private Calendar _dateTime = Calendar.getInstance();
    private Date lastSentNotification = new Date(0);
    private boolean sentNotification = false;

    private final long DAY_IN_MILLISECONDS = 86400000;

    private static HomeTabFragment _hFragment = new HomeTabFragment();

    public static HomeTabFragment getInstance() {
        return _hFragment;
    }

    public HomeTabFragment() {

    }

    public static HomeTabFragment newInstance(int sectionNumber) {
        HomeTabFragment fragment = getInstance();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        _db = new DBHandler(getContext());
        _todayWorkoutTextView = (TextView) rootView.findViewById(R.id.today_workout_title);
        _nextWorkoutTextView = (TextView) rootView.findViewById(R.id.next_workout_title);
        _prevWorkoutTextView = (TextView) rootView.findViewById(R.id.previous_workout_title);

        _todayWorkoutCard = (CardView) rootView.findViewById(R.id.today_workout_card_view);
        _nextWorkoutCard = (CardView) rootView.findViewById(R.id.next_workout_card_view);
        _prevWorkoutCard = (CardView) rootView.findViewById(R.id.previous_workout_card_view);

        _dateTime.setTime(CalendarDay.today().getDate());
        _sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        updateHomePage();

        _todayWorkoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_todayWorkout != null) {
                    startViewWorkoutActivity(_todayWorkout, _dateTime.getTimeInMillis());
                } else {
                    startViewDayActivity(_dateTime.getTimeInMillis());
                }
            }
        });

        _nextWorkoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_nextWorkout != null) {
                    startViewWorkoutActivity(_nextWorkout, _nextWorkout.getDate().getTime());
                } else {
                    startViewDayActivity(_dateTime.getTimeInMillis() + DAY_IN_MILLISECONDS);
                }
            }
        });

        _prevWorkoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_prevWorkout != null) {
                    startViewWorkoutActivity(_prevWorkout, _prevWorkout.getDate().getTime());
                } else {
                    startViewDayActivity(_dateTime.getTimeInMillis() - DAY_IN_MILLISECONDS);
                }
            }
        });
        setNotification();
        return rootView;
    }

    public void updateHomePage() {
        DateFormat df = DateFormat.getDateInstance();
        _todayWorkout = _db.getWorkoutForDay(CalendarDay.today().getDate());
        if(_todayWorkout != null) {
            _todayWorkoutTextView.setText(String.format(Locale.US, "%s on %s",
                    setTextViewText(_todayWorkout.getWorkoutPlanName(), -1), df.format(_todayWorkout.getDate().getTime())));
        } else {
            _todayWorkoutTextView.setText(setTextViewText("Add a workout to",  new Date().getTime()));
        }

        _nextWorkout = _db.getNextWorkoutAfterDay(CalendarDay.today().getDate());
        if(_nextWorkout != null) {
            _nextWorkoutTextView.setText(String.format(Locale.US, "%s on %s",
                    setTextViewText(_nextWorkout.getWorkoutPlanName(), -1), df.format(_nextWorkout.getDate().getTime())));
        } else {
            _nextWorkoutTextView.setText(setTextViewText("Add a workout to", new Date().getTime() + DAY_IN_MILLISECONDS));
        }

        _prevWorkout = _db.getPreviousWorkoutBeforeDay(CalendarDay.today().getDate());
        if(_prevWorkout != null) {
            _prevWorkoutTextView.setText(String.format(Locale.US, "%s on %s",
                    setTextViewText(_prevWorkout.getWorkoutPlanName(), -1), df.format(_prevWorkout.getDate().getTime())));
        } else {
            _prevWorkoutTextView.setText(setTextViewText("Add a workout to", new Date().getTime() - DAY_IN_MILLISECONDS));
        }
    }

    private String setTextViewText(String text, long date) {
        DateFormat df = DateFormat.getDateInstance();
        if(date > 0)
            return String.format("%s %s",
                    text,
                    df.format(date)
            );
        else
            return text;
    }
    //TODO: Bug - workouts appearing even when deleted from calendar
    private void startViewDayActivity(long time) {
        Intent intent = new Intent(getContext(), ViewDay.class);
        intent.putExtra("VIEW_DAY_PARENT", "Home");
        intent.putExtra("VIEW_DAY_DATETIME", time);
        startActivity(intent);
    }

    private void startViewWorkoutActivity(WorkoutPlan workoutPlan, long time) {
        Intent intent = new Intent(getContext(), ViewDay.class);
        intent.putExtra("VIEW_DAY_PARENT", "Home");
        intent.putExtra("VIEW_DAY_DATETIME", time);
        startActivity(intent);
    }

    private void startViewWorkoutActivity(WorkoutPlan workoutPlan) {
        Intent intent = new Intent(getContext(), ViewWorkout.class);
        intent.putExtra("VIEW_WORKOUT_PARENT", "Home");
        intent.putExtra(WorkoutPlanAdapter.WORKOUT_PLAN, JsonSerializer.workoutPlanToJson(workoutPlan));
        getContext().startActivity(intent);
    }

    private void setNotification() {
        _dateTime.setTime(CalendarDay.today().getDate());
        if(!sentNotification
                && _todayWorkout != null
                && lastSentNotification.getTime() < _dateTime.getTimeInMillis()
                && _sp.getBoolean("receive_notifications", true)
        ){
            lastSentNotification.setTime(_dateTime.getTimeInMillis());

            Intent viewDay = new Intent(getContext(), ViewDay.class);
            viewDay.putExtra("VIEW_DAY_DATETIME", _dateTime.getTimeInMillis());
            viewDay.putExtra("VIEW_DAY_PARENT", "Home");

            PugNotification.with(getContext())
                    .load()
                    .title("Today's Workout")
                    .message("Tap to see your workout for today")
                    .smallIcon(R.drawable.ic_workout_icon_24dp)
                    .largeIcon(R.drawable.ic_workout_icon_24dp)
                    .flags(Notification.DEFAULT_ALL)
                    .when(_todayWorkout.getDate().getTime())
                    .onlyAlertOnce(true)
                    .autoCancel(true)
                    .click(PendingIntent.getActivity(getContext(), 0, viewDay, PendingIntent.FLAG_UPDATE_CURRENT))
                    .simple()
                    .build();
            sentNotification = true;
        }
    }
}