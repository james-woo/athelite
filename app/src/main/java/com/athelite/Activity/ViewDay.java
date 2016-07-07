package com.athelite.Activity;

import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.athelite.Adapter.ExerciseListAdapter;
import com.athelite.Database.DBHandler;
import com.athelite.Dialog.EmptyTemplatesDialog;
import com.athelite.Dialog.ErrorDialog;
import com.athelite.Dialog.PickWorkout;
import com.athelite.Model.Exercise;
import com.athelite.Model.WorkoutPlan;
import com.athelite.R;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;

public class ViewDay extends AppCompatActivity implements DialogInterface.OnDismissListener{

    private FloatingActionButton _fab;
    private TextView _addAWorkoutTextView;
    private TextView _addAWorkoutTextViewHelp;
    private TextView _calendarTitle;
    private DBHandler _db;
    private WorkoutPlan _workoutDay;
    private ArrayList<Exercise> _workoutDayExercises = new ArrayList<>();
    private ExerciseListAdapter _adapter;
    private EditText _workoutName;
    private long _dateTime;
    private ListView _listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_day);

        initToolbar();
        initInstances();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.view_day_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("VIEW_DAY_PARENT"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initInstances() {
        Intent intent = getIntent();
        DateFormat df = DateFormat.getDateInstance();
        _db = new DBHandler(this);
        _adapter = new ExerciseListAdapter(this, _workoutDayExercises, _workoutDay);
        _dateTime = intent.getLongExtra("VIEW_DAY_DATETIME", 0);
        _workoutDay = _db.getWorkoutForDay(new Date(_dateTime));

        _fab = (FloatingActionButton) findViewById(R.id.view_day_fab);
        _calendarTitle = (TextView)findViewById(R.id.view_day_title);
        _calendarTitle.setText(df.format(new Date(_dateTime)));
        _addAWorkoutTextView = (TextView) findViewById(R.id.view_day_add_a_workout);
        _addAWorkoutTextViewHelp = (TextView) findViewById(R.id.view_day_add_workout_help);
        _workoutName = (EditText) findViewById(R.id.view_day_edit_workout_name);

        _listView = (ListView) findViewById(R.id.view_day_exercise_list_view);
        if(_listView != null)
            _listView.setAdapter(_adapter);

        setFabPickWorkout();

        updateDay();
    }

    void setFabPickWorkout(){
        _fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                if(_db.getWorkoutPlans().isEmpty()) {
                    EmptyTemplatesDialog emptyFragment = new EmptyTemplatesDialog();
                    Bundle args = new Bundle();
                    args.putLong("PickWorkout.dateTime", _dateTime);
                    emptyFragment.setArguments(args);
                    emptyFragment.show(fm, "Add A Template");
                } else {
                    PickWorkout dialogFragment = new PickWorkout();
                    Bundle args = new Bundle();
                    args.putLong("PickWorkout.dateTime", _dateTime);
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
                try {
                    addExercise();
                } catch (Exception e) {
                    ErrorDialog.logError("Error: ViewDay setFabAddNewExercise", e.getMessage());
                }
            }
        });
    }

    void updateDay() throws NullPointerException{
        try {
            if (_workoutDay != null) {
                setFabAddNewExercise();
                DateFormat df = DateFormat.getDateInstance();
                _calendarTitle.setText(new StringBuilder()
                        .append(df.format(new Date(_dateTime)))
                );
                _workoutName.setText(_workoutDay.getWorkoutPlanName());
                _workoutName.setFocusable(true);
                _workoutName.setFocusableInTouchMode(true);
                _workoutName.setSelectAllOnFocus(true);
                _workoutDay.setExercises(_db.getExercisesForWorkoutPlan(_workoutDay));
                _workoutDayExercises = _workoutDay.getWorkoutPlanExercises();
                _adapter.updateExerciseList(_workoutDayExercises);
                _adapter.setWorkout(_workoutDay);
                _adapter.notifyDataSetChanged();
                _addAWorkoutTextView.setVisibility(View.INVISIBLE);
                _addAWorkoutTextViewHelp.setVisibility(View.INVISIBLE);
            } else {
                setFabPickWorkout();
                DateFormat df = DateFormat.getDateInstance();
                _calendarTitle.setText(df.format(new Date(_dateTime)));
                _workoutName.setText(R.string.no_workout_selected);
                _workoutName.setFocusable(false);
                _addAWorkoutTextView.setVisibility(View.VISIBLE);
                _addAWorkoutTextViewHelp.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            ErrorDialog.logError("Error: ViewDay updateDay", e.getMessage());
        }
    }

    private void addExercise() throws NullPointerException {
        try {
            if (_workoutDay != null) {
                Exercise newExercise = _db.createExerciseForWorkoutPlanId(_workoutDay.getId());
                _adapter.addExercise(newExercise);
                _adapter.notifyDataSetChanged();
                _listView.smoothScrollToPositionFromTop(_adapter.getCount(), 0, 2);
            }
        } catch (Exception e) {
            ErrorDialog.logError("Error: ViewDay addExercise", e.getMessage());
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        try {
            if (_workoutDay != null) {
                _workoutDay.setExercises(_db.getExercisesForWorkoutPlan(_workoutDay));
                _adapter.updateExerciseList(_workoutDay.getWorkoutPlanExercises());
                _adapter.setWorkout(_workoutDay);
                _adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            ErrorDialog.logError("Error: ViewDay onRestart", e.getMessage());
        }
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        try {
            _workoutDay = _db.readWorkoutForDateTime(_dateTime);
            updateDay();
        } catch (Exception e) {
            ErrorDialog.logError("Error: ViewDay onDismiss", e.getMessage());
        }
    }

    public void updateWorkoutPlan() {
        try {
            if(_workoutDay != null) {
                _workoutDay.setWorkoutPlanName(_workoutName.getText().toString());
                _workoutDay.setExercises(_adapter.getExerciseList());
                _adapter.setWorkout(_workoutDay);
                _db.updateWorkoutPlan(_workoutDay);
                _db.updateWorkoutDay(_workoutDay);
            }
        } catch (Exception e) {
            ErrorDialog.logError("Error: ViewDay updateWorkoutPlan", e.getMessage());
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        try {
            updateWorkoutPlan();
        } catch (Exception e) {
            ErrorDialog.logError("Error: ViewDay onBackPressed", e.getMessage());
        }

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        if (fm.getBackStackEntryCount() > 0) {
            fm.popBackStack();
        } else if (fm.getBackStackEntryCount() == 0) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.action_delete_workout:
                _db.deleteWorkoutDay(_workoutDay);
                _adapter.clear();
                _workoutDayExercises.clear();
                _adapter.setWorkout(null);
                _workoutDay = null;
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_workout, menu);
        return true;
    }

}
