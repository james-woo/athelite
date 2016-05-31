package com.jameswoo.athelite.Activity;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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
import com.jameswoo.athelite.Tabs.CalendarTabFragment;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;

public class ViewDay extends AppCompatActivity implements DialogInterface.OnDismissListener{

    //private Calendar _calendar = new GregorianCalendar();
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
                addExercise();
            }
        });
    }

    void updateDay() {
        if(_workoutDay != null) {
            setFabAddNewExercise();
            DateFormat df = DateFormat.getDateInstance();
            _calendarTitle.setText(new StringBuilder()
                    .append(df.format(new Date(_dateTime)))
            );
            _workoutName.setText(_workoutDay.getWorkoutPlanName());
            _workoutName.setFocusable(true);
            _workoutName.setFocusableInTouchMode(true);
            _workoutName.setSelectAllOnFocus(true);
            _workoutDayExercises = _workoutDay.getWorkoutPlanExercises();
            _adapter.updateExerciseList(_workoutDayExercises);
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
    }

    private void addExercise() {
        if(_workoutDay != null) {
            Exercise newExercise = _db.createExerciseForWorkoutPlan(_db.getWritableDatabase(), _workoutDay);
            _adapter.addExercise(newExercise);
            _adapter.notifyDataSetChanged();
            _listView.smoothScrollToPositionFromTop(_adapter.getCount(), 0, 2);
        }
    }

    @Override
    public void onRestart() {
        super.onRestart();
        if(_workoutDay != null) {
            _workoutDay.setExercises(_db.getExercisesForWorkoutPlan(_workoutDay));
            _adapter.updateExerciseList(_workoutDay.getWorkoutPlanExercises());
            _adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        _workoutDay = _db.readWorkoutForDateTime(_dateTime);
        updateDay();
    }

    public void updateWorkoutPlan() {
        if(_workoutDay != null) {
            _workoutDay.setWorkoutPlanName(_workoutName.getText().toString());
            _workoutDay.setExercises(_adapter.getExerciseList());
            _db.updateWorkoutPlan(_workoutDay);
            CalendarTabFragment.getInstance().updateSelectedDate(new Date(_dateTime));
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
            case R.id.action_settings:
                Intent settingsIntent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.action_delete_workout:
                _db.deleteWorkoutDay(_workoutDay);
                CalendarTabFragment.getInstance().unSetSelectedDate(new Date(_dateTime));
                CalendarTabFragment.getInstance().setSelectedDate(CalendarDay.today().getDate());
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
