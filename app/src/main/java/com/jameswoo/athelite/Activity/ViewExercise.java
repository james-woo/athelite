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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.jameswoo.athelite.Adapter.ExerciseListAdapter;
import com.jameswoo.athelite.Adapter.ExerciseSetListAdapter;
import com.jameswoo.athelite.AutoComplete.ExerciseAutoCompleteTextChangedListener;
import com.jameswoo.athelite.AutoComplete.ExerciseAutoCompleteView;
import com.jameswoo.athelite.Database.DBExerciseList;
import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.Model.ExerciseSet;
import com.jameswoo.athelite.R;
import com.jameswoo.athelite.Util.JsonSerializer;

import java.util.ArrayList;
import java.util.List;

public class ViewExercise extends AppCompatActivity {

    private Exercise _exercise;
    private ExerciseAutoCompleteView _exerciseName;
    private ExerciseSetListAdapter _adapter;
    private DBHandler _db;
    private DBExerciseList _dbe;
    private ArrayAdapter _exerciseNameAdapter;
    private ListView _listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_exercise);

        initToolbar();
        initExercise();
        initInstances();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.back_to_exercises_page);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    void initInstances() {
        _db = new DBHandler(this);
        _dbe = new DBExerciseList(this);
        _adapter = new ExerciseSetListAdapter(this, _exercise.getExerciseSets());
        _listView = (ListView) findViewById(R.id.set_list_view);
        if(_listView != null)
            _listView.setAdapter(_adapter);

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        if(fabAdd != null)
            fabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Added new set", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    addExerciseSet();
                }
            });

        FloatingActionButton fabRemove = (FloatingActionButton) findViewById(R.id.fab_remove);
        if(fabRemove != null)
            fabRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Removed last set", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    _db.deleteExerciseSet(_adapter.getExerciseSets().get(_adapter.getExerciseSets().size() - 1));
                    _adapter.removeLastExerciseSet();
                    _adapter.notifyDataSetChanged();
                }
            });
    }

    void initExercise() {
        Intent intent = getIntent();
        String workoutPlanExerciseJson = intent.getStringExtra(ExerciseListAdapter.WORKOUT_EXERCISE);
        _exercise = JsonSerializer.getExerciseFromJson(workoutPlanExerciseJson);
        try {
            _exerciseName = (ExerciseAutoCompleteView) findViewById(R.id.edit_exercise_name);
            if(_exerciseName != null)
                _exerciseName.setSelectAllOnFocus(true);
            if(_exercise.getExerciseName() != null) {
                _exerciseName.setText(_exercise.getExerciseName());
            } else {
                _exerciseName.setText(R.string.new_exercise);
            }
            _exerciseName.addTextChangedListener(new ExerciseAutoCompleteTextChangedListener(this) {
                @Override
                public void onTextChanged(CharSequence userInput, int start, int before, int count) {
                    ArrayList<String> items = _dbe.findExercises(userInput.toString());
                    _exerciseNameAdapter.notifyDataSetChanged();
                    _exerciseNameAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.exercise_list_dropdown, items);
                    _exerciseName.setAdapter(_exerciseNameAdapter);
                }
            });
            _exerciseNameAdapter = new ArrayAdapter<>(this, R.layout.exercise_list_dropdown, new String[]{"Please search..."});
            _exerciseName.setAdapter(_exerciseNameAdapter);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void addExerciseSet() {
        ExerciseSet newSet = _db.createExerciseSetForExercise(_db.getWritableDatabase(), _exercise, getNextSetNumber());
        _adapter.addExerciseSet(newSet);
        _adapter.notifyDataSetChanged();
        _listView.smoothScrollToPositionFromTop(_adapter.getCount(), 0, 2);
    }

    private void updateSets() {
        for(int i = 0; i < _listView.getCount(); i++) {
            View item  = _listView.getChildAt(i);
            EditText setWeight = (EditText) item.findViewById(R.id.set_weight);
            EditText setReps = (EditText) item.findViewById(R.id.set_reps);
            _adapter.getItem(i).setSetWeight(Double.valueOf(setWeight.getText().toString()));
            _adapter.getItem(i).setSetReps(Integer.valueOf(setReps.getText().toString()));
        }
    }

    private int getNextSetNumber() {
        return _adapter.getExerciseSets().size() + 1;
    }

    public void updateExercise() {
        updateSets();
        if(_exerciseName.getText().toString().equals("")) {
            _exerciseName.setText(R.string.new_exercise);
        }
        _exercise.calculateOneRepMax();
        _exercise.setExerciseName(_exerciseName.getText().toString());
        _exercise.setExerciseSets(_adapter.getExerciseSets());
        _db.updateExercise(_exercise);
        _dbe.createExercise(_exerciseName.getText().toString());
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        updateExercise();

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
        }
        return true;
    }

}
