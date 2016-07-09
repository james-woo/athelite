package com.athelite.Tabs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.athelite.Database.DBHandler;
import com.athelite.Dialog.ErrorDialog;
import com.athelite.Model.WorkoutPlan;
import com.athelite.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalendarTabFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private MaterialCalendarView _calendar;
    private Calendar _dateTime = Calendar.getInstance();
    private DBHandler _db;
    private TextView _currentlySelectedDate;

    private static CalendarTabFragment _cFragment = new CalendarTabFragment();

    public static CalendarTabFragment getInstance() {
        return _cFragment;
    }

    public CalendarTabFragment() {

    }

    public static CalendarTabFragment newInstance(int sectionNumber) {
        CalendarTabFragment fragment = getInstance();
        if(!fragment.isAdded()) {
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
        }
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_calendar, container, false);
        _db = new DBHandler(getContext());
        _calendar = (MaterialCalendarView) rootView.findViewById(R.id.calendar_tab_calendar_view);
        _calendar.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        _dateTime.setTime(CalendarDay.today().getDate());
        _calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                _dateTime.setTime(date.getDate());
                _calendar.clearSelection();
                setSelectedDates(date);
            }

        });

        _currentlySelectedDate = (TextView) rootView.findViewById(R.id.calendar_tab_currently_selected_date);
        setSelectedDates(CalendarDay.today());
        updateSelectedDate();
        return rootView;
    }

    public void updateCalendar() throws NullPointerException {
        try {
            ArrayList<Date> workoutDays = _db.getWorkoutDays();
            for (Date day : workoutDays) {
                _calendar.setDateSelected(day, true);
            }
        } catch(Exception e) {
            ErrorDialog.logError("Error: updateCalendar", e.getMessage());
        }
    }

    public void unSetSelectedDate(Date selectedDate) throws NullPointerException {
        try {
            if (_calendar != null) {
                _calendar.setDateSelected(selectedDate, false);
            }
        } catch(Exception e) {
            ErrorDialog.logError("Error: unSetSelectedDate", e.getMessage());
        }
    }

    public void setSelectedDate(Date selectedDate) throws NullPointerException {
        try {
            if (_calendar != null) {
                _calendar.setDateSelected(selectedDate, true);
            }
        } catch(Exception e) {
            ErrorDialog.logError("Error: setSelectedDate", e.getMessage());
        }
    }

    private void setSelectedDates(CalendarDay selectedDate) throws NullPointerException {
        try {
            if (_calendar != null) {
                updateCalendar();
                _calendar.setDateSelected(selectedDate, true);
                updateSelectedDate();
            }
        } catch(Exception e) {
            ErrorDialog.logError("Error: setSelectedDates", e.getMessage());
        }
    }

    private void updateSelectedDate() throws NullPointerException {
        try {
            DateFormat df = DateFormat.getDateInstance();
            if(_db != null) {
                WorkoutPlan workoutPlan = _db.getWorkoutForDay(_dateTime.getTime());
                if (workoutPlan != null) {
                    _currentlySelectedDate.setText(String.format("%s %s", df.format(_dateTime.getTimeInMillis()), workoutPlan.getWorkoutPlanName()));
                } else {
                    _currentlySelectedDate.setText(String.format("Selected %s", df.format(_dateTime.getTimeInMillis())));
                }
            }
        } catch(Exception e) {
            ErrorDialog.logError("Error: updateSelectedDate", e.getMessage());
        }
    }

    public void updateSelectedDate(Date selectedDate) throws NullPointerException {
        try {
            DateFormat df = DateFormat.getDateInstance();
            if (_db != null) {
                WorkoutPlan workoutPlan = _db.getWorkoutForDay(selectedDate);
                if (workoutPlan != null) {
                    _currentlySelectedDate.setText(String.format("%s %s", df.format(_dateTime.getTimeInMillis()), workoutPlan.getWorkoutPlanName()));
                } else {
                    _currentlySelectedDate.setText(String.format("Selected %s", df.format(_dateTime.getTimeInMillis())));
                }
            }
        } catch(Exception e) {
            ErrorDialog.logError("Error: updateSelectedDate", e.getMessage());
        }
    }


    public long getDateTimeInMilliseconds() throws NullPointerException {
        try {
            return _dateTime.getTimeInMillis();
        } catch(Exception e) {
            ErrorDialog.logError("Error: getDateTimeInMilliseconds", e.getMessage());
        }
        return CalendarDay.today().getDate().getTime();
    }
}
