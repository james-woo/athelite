package com.jameswoo.athelite.Activity;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jameswoo.athelite.Adapter.ExerciseListAdapter;
import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.Dialog.EmptyTemplatesDialog;
import com.jameswoo.athelite.Dialog.PickWorkout;
import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.Model.WorkoutPlan;
import com.jameswoo.athelite.R;


import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class ViewDay extends AppCompatActivity implements DialogInterface.OnDismissListener{

    private Calendar _calendar = new GregorianCalendar();
    private FloatingActionButton _fab;
    private TextView _selectedWorkoutTextView;
    private TextView _calendarTitle;
    private DBHandler _db;
    private WorkoutPlan _workoutDay;
    private ArrayList<Exercise> _workoutDayExercises = new ArrayList<>();
    private ExerciseListAdapter _adapter;
    private EditText _workoutName;

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
            getSupportActionBar().setTitle(getIntent().getStringExtra("VIEW_DAY_PARENT"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initInstances() {
        Intent intent = getIntent();
        _calendar.setTimeInMillis(intent.getLongExtra("DATETIME", 0));
        _calendarTitle = (TextView)findViewById(R.id.calendar_title);
        DateFormat df = DateFormat.getDateInstance();
        _calendarTitle.setText(df.format(new Date(_calendar.getTimeInMillis())));

        _db = new DBHandler(this);
        _workoutDay = _db.readWorkoutForDateTime(_calendar.getTimeInMillis());

        _adapter = new ExerciseListAdapter(this, _workoutDayExercises);
        ListView listView = (ListView) findViewById(R.id.workoutday_exercise_list_view);
        listView.setAdapter(_adapter);

        _fab = (FloatingActionButton) findViewById(R.id.fab_pick_workout);

        setFabPickWorkout();

        _selectedWorkoutTextView = (TextView) findViewById(R.id.selected_workout_plan);
        _workoutName = (EditText) findViewById(R.id.view_day_edit_workout_name);
        updateDay();
    }

    void setFabPickWorkout(){
        _fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if(_db.getWorkoutPlans().isEmpty()) {
                    EmptyTemplatesDialog emptyFragment = new EmptyTemplatesDialog();
                    emptyFragment.show(fm, "Add A Template");
                } else {
                    PickWorkout dialogFragment = new PickWorkout();
                    Bundle args = new Bundle();
                    args.putLong("PickWorkout.dateTime", _calendar.getTimeInMillis());
                    dialogFragment.setArguments(args);
                    dialogFragment.show(fm, "Select A Template");
                }
            }
        });
    }

    void setFabAddNewExercise() {
        _fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "Added new exercise", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                addExercise();
            }
        });
    }

    void updateDay() {
        if(_workoutDay != null) {
            setFabAddNewExercise();
            DateFormat df = DateFormat.getDateInstance();
            _calendarTitle.setText(new StringBuilder()
                    .append(df.format(new Date(_calendar.getTimeInMillis())))
            );
            _workoutName.setText(_workoutDay.getWorkoutPlanName());
            _workoutName.setFocusable(true);
            _workoutName.setFocusableInTouchMode(true);
            _workoutName.setSelectAllOnFocus(true);
            _workoutDayExercises = _workoutDay.getWorkoutPlanExercises();
            _adapter.updateExerciseList(_workoutDayExercises);
            _adapter.notifyDataSetChanged();
            _selectedWorkoutTextView.setVisibility(View.INVISIBLE);
        } else {
            setFabPickWorkout();
            DateFormat df = DateFormat.getDateInstance();
            _calendarTitle.setText(df.format(new Date(_calendar.getTimeInMillis())));
            _workoutName.setText("No workout selected");
            _workoutName.setFocusable(false);
            _selectedWorkoutTextView.setVisibility(View.VISIBLE);
            _selectedWorkoutTextView.setText(R.string.add_a_workout);
        }
    }

    private void addExercise() {
        Exercise newExercise = _db.createExerciseForWorkoutPlan(_db.getWritableDatabase(), _workoutDay);
        _adapter.addExercise(newExercise);
        _adapter.notifyDataSetChanged();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        _workoutDay.setExercises(_db.getExercisesForWorkoutPlan(_workoutDay));
        _adapter.updateExerciseList(_workoutDay.getWorkoutPlanExercises());
        _adapter.notifyDataSetChanged();
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        _workoutDay = _db.readWorkoutForDateTime(_calendar.getTimeInMillis());
        updateDay();
    }

    public void updateWorkoutPlan() {
        if(_workoutDay != null) {
            _workoutDay.setWorkoutPlanName(_workoutName.getText().toString());
            _workoutDay.setExercises(_adapter.getExerciseList());
            _db.updateWorkoutPlan(_workoutDay);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        updateWorkoutPlan();

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
