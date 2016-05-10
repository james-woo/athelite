package com.jameswoo.athelite.Activity;

import android.app.FragmentManager;
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

import com.jameswoo.athelite.Adapter.ExerciseListAdapter;
import com.jameswoo.athelite.Adapter.WorkoutPlanAdapter;
import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.Model.WorkoutPlan;
import com.jameswoo.athelite.R;
import com.jameswoo.athelite.Util.JsonSerializer;

public class ViewWorkout extends AppCompatActivity {
    private WorkoutPlan _workoutPlan;
    private EditText _workoutName;
    private ExerciseListAdapter _adapter;
    private DBHandler _db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_workout);

        initToolbar();
        initWorkoutPlan();
        initInstances();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        _workoutName = (EditText) findViewById(R.id.edit_workout_name);
        if(_workoutName != null) {
            _workoutName.setSelectAllOnFocus(true);
            _workoutName.setText(_workoutPlan.getWorkoutPlanName());
        } else {
                _workoutName.setText(R.string.new_workout);
        }
    }

    void initInstances() {
        _db = new DBHandler(this);
        _adapter = new ExerciseListAdapter(this, _workoutPlan.getWorkoutPlanExercises(), _workoutPlan);
        ListView listView = (ListView) findViewById(R.id.exercise_list_view);
        if(listView != null)
            listView.setAdapter(_adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
        Exercise newExercise = _db.createExerciseForWorkoutPlan(_db.getWritableDatabase(), _workoutPlan);
        _adapter.addExercise(newExercise);
        _adapter.notifyDataSetChanged();
    }

    public void updateWorkoutPlan() {
        if(_workoutName.getText().toString().equals("")) {
            _workoutName.setText(R.string.new_workout);
        }
        _workoutPlan.setWorkoutPlanName(_workoutName.getText().toString());
        _workoutPlan.setExercises(_adapter.getExerciseList());
        _db.updateWorkoutPlan(_workoutPlan);
    }

    @Override
    public void onResume() {
        super.onResume();
        _workoutPlan.setExercises(_db.getExercisesForWorkoutPlan(_workoutPlan));
        _adapter.updateExerciseList(_workoutPlan.getWorkoutPlanExercises());
        _adapter.notifyDataSetChanged();
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
