package com.athelite.Activity;

import android.app.FragmentManager;
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
import android.widget.EditText;
import android.widget.ListView;

import com.athelite.Adapter.ExerciseListAdapter;
import com.athelite.Adapter.WorkoutPlanAdapter;
import com.athelite.Database.DBHandler;
import com.athelite.Dialog.ErrorDialog;
import com.athelite.Model.Exercise;
import com.athelite.Model.ExerciseSet;
import com.athelite.Model.WorkoutPlan;
import com.athelite.R;
import com.athelite.Tabs.WorkoutPlanTabFragment;
import com.athelite.Util.JsonSerializer;

public class ViewWorkout extends AppCompatActivity {
    private WorkoutPlan _workoutPlan;
    private EditText _workoutName;
    private ExerciseListAdapter _adapter;
    private DBHandler _db;
    private ListView _listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workout);

        initToolbar();
        initWorkoutPlan();
        initInstances();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.view_workout_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("VIEW_WORKOUT_PARENT"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    void initWorkoutPlan() {
        Intent intent = getIntent();
        String workoutPlanJson = intent.getStringExtra(WorkoutPlanAdapter.WORKOUT_PLAN);
        _workoutPlan = JsonSerializer.getWorkoutPlanFromJson(workoutPlanJson);

        _workoutName = (EditText) findViewById(R.id.view_workout_edit_name);
        if (_workoutName != null) {
            if (!_workoutName.getText().toString().isEmpty()) {
                _workoutName.setSelectAllOnFocus(true);
                _workoutName.setText(_workoutPlan.getWorkoutPlanName());
            } else {
                _workoutName.setText(R.string.new_workout);
            }
        }
    }

    void initInstances() {
        _db = new DBHandler(this);
        _adapter = new ExerciseListAdapter(this, _workoutPlan.getWorkoutPlanExercises(), _workoutPlan);
        _listView = (ListView) findViewById(R.id.view_workout_exercise_list_view);
        if(_listView != null)
            _listView.setAdapter(_adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.view_workout_fab);
        if(fab != null)
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Added new exercise", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    addExercise();
                }
            });
    }

    private void addExercise() {
        Exercise newExercise = _db.createExerciseForWorkoutPlanId(_db.getWritableDatabase(), _workoutPlan.getId());
        _adapter.addExercise(newExercise);
        _adapter.notifyDataSetChanged();
        _listView.smoothScrollToPositionFromTop(_adapter.getCount(), 0, 2);
    }

    public void updateWorkoutPlan() {
        if(_workoutPlan != null) {
            if (_workoutName.getText().toString().equals("")) {
                _workoutName.setText(R.string.new_workout);
            }
            _workoutPlan.setWorkoutPlanName(_workoutName.getText().toString());
            _workoutPlan.setExercises(_adapter.getExerciseList());
            _adapter.setWorkout(_workoutPlan);
            _db.updateWorkoutPlan(_workoutPlan);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            if (_workoutPlan != null) {
                _workoutPlan.setExercises(_db.getExercisesForWorkoutPlan(_workoutPlan));
                _adapter.updateExerciseList(_workoutPlan.getWorkoutPlanExercises());
                _adapter.setWorkout(_workoutPlan);
                _adapter.notifyDataSetChanged();
            }
        } catch(Exception e) {
            ErrorDialog.messageBox("Error Viewing Workout", e.getMessage(), this);
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
                _db.deleteWorkoutPlan(_workoutPlan);
                WorkoutPlanTabFragment.getInstance().deleteWorkout(_workoutPlan);
                _workoutPlan = null;
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
