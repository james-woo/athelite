package com.jameswoo.athelite.Tabs;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.R;
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

        _dateTime.setTime(CalendarDay.today().getDate());
        _calendar.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                _dateTime.setTime(date.getDate());
                _calendar.clearSelection();
                setSelectedDates(date);
            }

        });

        _currentlySelectedDate = (TextView) rootView.findViewById(R.id.currently_selected_date);
        setSelectedDates(CalendarDay.today());
        updateSelectedDate();
        return rootView;
    }

    public void updateCalendar() {
        ArrayList<Date> workoutDays = _db.getWorkoutDays();
        for (Date day : workoutDays) {
            _calendar.setDateSelected(day, true);
        }
    }

    public void unSetSelectedDate(Date selectedDate) {
        if (_calendar != null) {
            _calendar.setDateSelected(selectedDate, false);
        }
    }

    public void setSelectedDate(Date selectedDate) {
        if (_calendar != null) {
            _calendar.setDateSelected(selectedDate, true);
        }
    }

    private void setSelectedDates(CalendarDay selectedDate) {
        if (_calendar != null) {
            updateCalendar();
            _calendar.setDateSelected(selectedDate, true);
            updateSelectedDate();
        }
    }

    private void updateSelectedDate() {
        DateFormat df = DateFormat.getDateInstance();
        _currentlySelectedDate.setText(String.format("Selected %s", df.format(_dateTime.getTimeInMillis())));
    }

    public long getDateTimeInMilliseconds() {
        return _dateTime.getTimeInMillis();
    }
}
