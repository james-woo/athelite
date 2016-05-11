package com.jameswoo.athelite.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jameswoo.athelite.Activity.ViewDay;
import com.jameswoo.athelite.Activity.ViewWorkout;
import com.jameswoo.athelite.Adapter.WorkoutPlanAdapter;
import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.Model.WorkoutPlan;
import com.jameswoo.athelite.R;
import com.jameswoo.athelite.Util.JsonSerializer;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

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

    private Calendar _dateTime = Calendar.getInstance();;

    private final long DAY_IN_MILLISECONDS = 86400000;


    public HomeTabFragment() {
    }

    public static HomeTabFragment newInstance(int sectionNumber) {
        HomeTabFragment fragment = new HomeTabFragment();
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

        updateHomePage();

        _todayWorkoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_todayWorkout != null) {
                    startViewWorkoutActivity(_todayWorkout);
                } else {
                    startViewDayActivity(_dateTime.getTimeInMillis());
                }
            }
        });

        _nextWorkoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_nextWorkout != null) {
                    startViewWorkoutActivity(_nextWorkout);
                } else {
                    startViewDayActivity(_dateTime.getTimeInMillis() + DAY_IN_MILLISECONDS);
                }
            }
        });

        _prevWorkoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_prevWorkout != null) {
                    startViewWorkoutActivity(_prevWorkout);
                } else {
                    startViewDayActivity(_dateTime.getTimeInMillis() - DAY_IN_MILLISECONDS);
                }
            }
        });

        return rootView;
    }

    public void updateHomePage() {
        _todayWorkout = _db.getWorkoutForDay(CalendarDay.today().getDate());
        if(_todayWorkout != null) {
            _todayWorkoutTextView.setText(setTextViewText(_todayWorkout.getWorkoutPlanName(), -1));
        } else {
            _todayWorkoutTextView.setText(setTextViewText("Add a workout to",  new Date().getTime()));
        }

        _nextWorkout = _db.getNextWorkoutAfterDay(CalendarDay.today().getDate());
        if(_nextWorkout != null) {
            _nextWorkoutTextView.setText(setTextViewText(_nextWorkout.getWorkoutPlanName(), -1));
        } else {
            _nextWorkoutTextView.setText(setTextViewText("Add a workout to", new Date().getTime() + DAY_IN_MILLISECONDS));
        }

        _prevWorkout = _db.getPreviousWorkoutBeforeDay(CalendarDay.today().getDate());
        if(_prevWorkout != null) {
            _prevWorkoutTextView.setText(setTextViewText(_prevWorkout.getWorkoutPlanName(), -1));
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
        intent.putExtra("DATETIME", time);
        startActivity(intent);
    }

    private void startViewWorkoutActivity(WorkoutPlan workoutPlan) {
        Intent intent = new Intent(getContext(), ViewWorkout.class);
        intent.putExtra("VIEW_WORKOUT_PARENT", "Home");
        intent.putExtra(WorkoutPlanAdapter.WORKOUT_PLAN, JsonSerializer.workoutPlanToJson(workoutPlan));
        getContext().startActivity(intent);
    }
}