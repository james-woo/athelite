package com.jameswoo.athelite.Activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jameswoo.athelite.Adapter.ExerciseListAdapter;
import com.jameswoo.athelite.Adapter.WorkoutPlanAdapter;
import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.Model.ExerciseSet;
import com.jameswoo.athelite.Model.WorkoutPlan;
import com.jameswoo.athelite.R;
import com.jameswoo.athelite.Util.JsonSerializer;

import java.util.ArrayList;

public class ViewWorkout extends AppCompatActivity {
    private String _workoutPlanJson;
    private WorkoutPlan _workoutPlan;
    private EditText _workoutName;
    private ArrayList<Exercise> _workoutExerciseList;
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
            getSupportActionBar().setTitle(R.string.back_to_workout_page);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    void initInstances() {
        // Create the adapter to convert the array to views
        _db = new DBHandler(this);
        _workoutExerciseList = _workoutPlan.getWorkoutPlanExercises();
        _adapter = new ExerciseListAdapter(this, _workoutExerciseList);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.exercise_list_view);
        listView.setAdapter(_adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Added new exercise", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                addExercise();
            }
        });
    }

    void initWorkoutPlan() {
        Intent intent = getIntent();
        _workoutPlanJson = intent.getStringExtra(WorkoutPlanAdapter.WORKOUT_PLAN);
        _workoutPlan = JsonSerializer.getWorkoutPlanFromJson(_workoutPlanJson);

        _workoutName = (EditText) findViewById(R.id.edit_workout_name);
        if(_workoutPlan.getWorkoutPlanName() != null) {
            _workoutName.setText(_workoutPlan.getWorkoutPlanName());
        } else {
            _workoutName.setText(R.string.new_workout);
        }
    }

    private void addExercise() {
        ExerciseSet exerciseSet1 = new ExerciseSet(1, 0, 0);
        ExerciseSet exerciseSet2 = new ExerciseSet(2, 0, 0);
        ExerciseSet exerciseSet3 = new ExerciseSet(3, 0, 0);
        Exercise newExercise = new Exercise.Builder("New Exercise")
                                    .exerciseSet(exerciseSet1)
                                    .exerciseSet(exerciseSet2)
                                    .exerciseSet(exerciseSet3)
                                    .build();
        _workoutExerciseList.add(newExercise);
        _db.addExercise(newExercise);
        _adapter.notifyDataSetChanged();
    }

    public void updateWorkoutPlan() {
        _workoutPlan.setWorkoutPlanName(_workoutName.getText().toString());

        _db.updateWorkoutPlan(_workoutPlan, _workoutExerciseList);
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
