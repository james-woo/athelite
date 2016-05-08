package com.jameswoo.athelite.Tabs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarTabFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private MaterialCalendarView _calendar;
    private Calendar _dateTime = Calendar.getInstance();
    private DBHandler _db;

    public CalendarTabFragment() {

    }

    public static CalendarTabFragment newInstance(int sectionNumber) {
        CalendarTabFragment fragment = new CalendarTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);

        _db = new DBHandler(getContext());

        _calendar = (MaterialCalendarView) rootView.findViewById(R.id.calendarView);

        _calendar.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);
        setSelectedDates(CalendarDay.today());
        _dateTime.setTime(CalendarDay.today().getDate());

        _calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                _dateTime.setTime(date.getDate());
                _calendar.clearSelection();
                setSelectedDates(date);
            }

        });

        return rootView;
    }

    private void setSelectedDates(CalendarDay selectedDate) {
        ArrayList<Date> workoutDays = _db.getWorkoutDays();
        for (Date day : workoutDays)
        {
            _calendar.setDateSelected(day, true);
        }
        _calendar.setDateSelected(selectedDate, true);
    }

    public long getDateTimeInMilliseconds() {
        return _dateTime.getTimeInMillis();
    }
}
