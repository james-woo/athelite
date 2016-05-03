package com.jameswoo.athelite.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.jameswoo.athelite.R;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class ViewDay extends AppCompatActivity {

    private Calendar _calendar = new GregorianCalendar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_day);

        initToolbar();
        initInstances();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.calendar_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.back_to_calendar_page);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initInstances() {
        Intent intent = getIntent();
        _calendar.setTimeInMillis(intent.getLongExtra("DATETIME", 0));
        TextView calendarTitle = (TextView)findViewById(R.id.calendar_title);
        calendarTitle.setText(_calendar.get(GregorianCalendar.YEAR) + " " + _calendar.get(GregorianCalendar.MONTH) + " " + _calendar.get(GregorianCalendar.DAY_OF_MONTH));
    }

}
