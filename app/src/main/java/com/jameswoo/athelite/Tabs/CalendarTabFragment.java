package com.jameswoo.athelite.Tabs;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import com.jameswoo.athelite.R;

import java.util.Calendar;
import java.util.Date;

public class CalendarTabFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private CalendarView _calendar;
    private Calendar _dateTime = Calendar.getInstance();

    public CalendarTabFragment() {

    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
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

        _calendar = (CalendarView) rootView.findViewById(R.id.calendarView);
        _dateTime.setTimeInMillis(_calendar.getDate());
        _calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                _dateTime.set(year, month, dayOfMonth);
            }
        });

        return rootView;
    }

    public long getDateTimeInMilliseconds() {
        return _dateTime.getTimeInMillis();
    }
}
