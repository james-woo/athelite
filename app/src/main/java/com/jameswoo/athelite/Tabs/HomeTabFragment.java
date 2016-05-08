package com.jameswoo.athelite.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jameswoo.athelite.Activity.ViewWorkout;
import com.jameswoo.athelite.Adapter.WorkoutPlanAdapter;
import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.Model.WorkoutPlan;
import com.jameswoo.athelite.R;
import com.jameswoo.athelite.Util.JsonSerializer;
import com.prolificinteractive.materialcalendarview.CalendarDay;

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

        updateHomePage();

        _todayWorkoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_todayWorkout != null) {
                    Intent intent = new Intent(getContext(), ViewWorkout.class);
                    intent.putExtra(WorkoutPlanAdapter.WORKOUT_PLAN, JsonSerializer.workoutPlanToJson(_todayWorkout));
                    getContext().startActivity(intent);
                }
            }
        });

        _nextWorkoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_nextWorkout != null) {
                    Intent intent = new Intent(getContext(), ViewWorkout.class);
                    intent.putExtra(WorkoutPlanAdapter.WORKOUT_PLAN, JsonSerializer.workoutPlanToJson(_nextWorkout));
                    getContext().startActivity(intent);
                }
            }
        });

        _prevWorkoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_prevWorkout != null) {
                    Intent intent = new Intent(getContext(), ViewWorkout.class);
                    intent.putExtra(WorkoutPlanAdapter.WORKOUT_PLAN, JsonSerializer.workoutPlanToJson(_prevWorkout));
                    getContext().startActivity(intent);
                }
            }
        });

        return rootView;
    }

    public void updateHomePage() {
        _todayWorkout = _db.getWorkoutForDay(CalendarDay.today().getDate());
        if(_todayWorkout != null) {
            _todayWorkoutCard.setVisibility(View.VISIBLE);
            _todayWorkoutTextView.setText(_todayWorkout.getWorkoutPlanName());
        } else {
            _todayWorkoutCard.setVisibility(View.INVISIBLE);
        }

        _nextWorkout = _db.getNextWorkoutAfterDay(CalendarDay.today().getDate());
        if(_nextWorkout != null) {
            _nextWorkoutCard.setVisibility(View.VISIBLE);
            _nextWorkoutTextView.setText(_nextWorkout.getWorkoutPlanName());
        } else {
            _nextWorkoutCard.setVisibility(View.INVISIBLE);
        }

        _prevWorkout = _db.getPreviousWorkoutBeforeDay(CalendarDay.today().getDate());
        if(_prevWorkout != null) {
            _prevWorkoutCard.setVisibility(View.VISIBLE);
            _prevWorkoutTextView.setText(_prevWorkout.getWorkoutPlanName());
        } else {
            _prevWorkoutCard.setVisibility(View.INVISIBLE);
        }
    }
}