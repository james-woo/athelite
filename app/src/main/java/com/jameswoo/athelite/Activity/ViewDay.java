package com.jameswoo.athelite.Activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.Dialog.PickWorkout;
import com.jameswoo.athelite.Model.WorkoutPlan;
import com.jameswoo.athelite.R;

import java.sql.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ViewDay extends AppCompatActivity implements DialogInterface.OnDismissListener{

    private Calendar _calendar = new GregorianCalendar();
    private FloatingActionButton _fab;
    private TextView _selectedWorkoutTextView;
    private DBHandler _db;
    private WorkoutPlan _workoutDay;

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
        DateFormat df = DateFormat.getDateInstance();
        calendarTitle.setText(df.format(new Date(intent.getLongExtra("DATETIME", 0))));

        _db = new DBHandler(this);
        _workoutDay = _db.readWorkoutForDateTime(_calendar.getTimeInMillis());

        _fab = (FloatingActionButton) findViewById(R.id.fab_pick_workout);
        _fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                PickWorkout dialogFragment = new PickWorkout ();
                Bundle args = new Bundle();
                args.putLong("PickWorkout.dateTime", _calendar.getTimeInMillis());
                dialogFragment.setArguments(args);
                dialogFragment.show(fm, "Select A Workout");
            }
        });

        _selectedWorkoutTextView = (TextView) findViewById(R.id.selected_workout_plan);
        if(_workoutDay != null) {
            _selectedWorkoutTextView.setVisibility(View.INVISIBLE);
            _fab.setImageResource(android.R.drawable.ic_menu_edit);
        } else {
            _selectedWorkoutTextView.setVisibility(View.VISIBLE);
            _fab.setImageResource(android.R.drawable.ic_input_add);
        }
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        //Fragment dialog had been dismissed
        _workoutDay = _db.readWorkoutForDateTime(_calendar.getTimeInMillis());
        if(_workoutDay != null) {
            _selectedWorkoutTextView.setText(_workoutDay.getWorkoutPlanName());
            _fab.setImageResource(android.R.drawable.ic_menu_edit);
        } else {
            _selectedWorkoutTextView.setText(R.string.add_a_workout);
            _fab.setImageResource(android.R.drawable.ic_input_add);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();

        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        }

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

}
