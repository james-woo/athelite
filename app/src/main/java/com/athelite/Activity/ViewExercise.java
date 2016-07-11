package com.athelite.Activity;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.athelite.Adapter.ExerciseListAdapter;
import com.athelite.Adapter.ExerciseSetListAdapter;
import com.athelite.AutoComplete.ExerciseAutoCompleteTextChangedListener;
import com.athelite.AutoComplete.ExerciseAutoCompleteView;
import com.athelite.Database.DBExerciseList;
import com.athelite.Database.DBHandler;
import com.athelite.Model.Exercise;
import com.athelite.Model.ExerciseSet;
import com.athelite.R;
import com.athelite.Util.JsonSerializer;
import com.athelite.Util.StringDifference;
import com.athelite.Util.TextValidator;

import java.util.ArrayList;
import java.util.Locale;

public class ViewExercise extends AppCompatActivity {

    private Exercise _exercise;
    private ExerciseAutoCompleteView _exerciseName;
    private ExerciseSetListAdapter _adapter;
    private DBHandler _db;
    private DBExerciseList _dbe;
    private ArrayAdapter _exerciseNameAdapter;
    private ListView _listView;

    private WorkoutCountDownTimer _exerciseTimer;
    private Button _exerciseTimerButton;
    private Button _exerciseTimerResetButton;
    private TextView _exerciseTimerMinuteText;
    private TextView _exerciseTimerSecondText;
    private boolean _timerHasStarted = false;

    private String _lastTimerMinute = "";
    private SharedPreferences _sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_exercise);

        initToolbar();
        initExercise();
        initInstances();
        timerInit();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.view_exercise_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.back_to_exercises_page);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    void initInstances() {
        _sp = PreferenceManager.getDefaultSharedPreferences(this);
        _db = new DBHandler(this);
        _dbe = new DBExerciseList(this);
        _adapter = new ExerciseSetListAdapter(this, _exercise.getExerciseSets());
        _listView = (ListView) findViewById(R.id.view_exercise_list_view);
        if(_listView != null)
            _listView.setAdapter(_adapter);

        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.view_exercise_fab);
        if(fabAdd != null) {
            fabAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Added new set", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    addExerciseSet();
                }
            });
        }
    }

    void timerInit() {
        _exerciseTimerButton = (Button) findViewById(R.id.view_exercise_timer_button);
        _exerciseTimerResetButton = (Button) findViewById(R.id.view_exercise_timer_reset_button);
        _exerciseTimerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start or Resume the timer
                if (!_timerHasStarted)
                {
                    String minutes = _exerciseTimerMinuteText.getText().toString();
                    String seconds = _exerciseTimerSecondText.getText().toString();
                    if(!_lastTimerMinute.equals(minutes)) {
                        _lastTimerMinute = minutes;
                        SharedPreferences.Editor edit = _sp.edit();
                        edit.putString("timer_minutes", minutes);
                        edit.putString("timer_seconds", seconds);
                        edit.apply();
                    }
                    _exerciseTimerMinuteText.setFocusable(false);
                    _exerciseTimerSecondText.setFocusable(false);
                    _exerciseTimerMinuteText.setFocusableInTouchMode(false);
                    _exerciseTimerSecondText.setFocusableInTouchMode(false);
                    _exerciseTimer = new WorkoutCountDownTimer(
                            getTime(minutes, seconds),
                            1000);

                    _exerciseTimer.start();
                    _timerHasStarted = true;
                    _exerciseTimerButton.setText(R.string.view_exercise_timer_pause);
                }
                // Pause the timer
                else
                {
                    _exerciseTimerMinuteText.setFocusableInTouchMode(true);
                    _exerciseTimerSecondText.setFocusableInTouchMode(true);
                    _exerciseTimer.cancel();
                    _timerHasStarted = false;
                    _exerciseTimerButton.setText(R.string.view_exercise_timer_resume);
                }
            }
        });
        _exerciseTimerResetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _exerciseTimerMinuteText.setFocusableInTouchMode(true);
                _exerciseTimerSecondText.setFocusableInTouchMode(true);
                _exerciseTimerButton.setText(R.string.view_exercise_timer_start);
                _exerciseTimer.cancel();
                _timerHasStarted = false;
                _exerciseTimerMinuteText.setText(_sp.getString("timer_minutes", "03"));
                _exerciseTimerSecondText.setText(_sp.getString("timer_seconds", "00"));
            }
        });
        _exerciseTimerMinuteText = (TextView) findViewById(R.id.view_exercise_timer_minutes);
        _exerciseTimerSecondText = (TextView) findViewById(R.id.view_exercise_timer_seconds);
        _exerciseTimerMinuteText.setText(_sp.getString("timer_minutes", "03"));
        _exerciseTimerSecondText.setText(_sp.getString("timer_seconds", "00"));

        _exerciseTimerMinuteText.addTextChangedListener(new TextValidator(_exerciseTimerMinuteText) {
            @Override
            public void validate(TextView textView, String text) {
                String before = this.getBefore();
                try{
                    int i = Integer.parseInt(text);
                } catch(Exception e) {
                    textView.setText(before);
                    return;
                }
                if(text.length() < 2) {
                    textView.setText(String.format(Locale.US,("%s%s"), "0", text));
                } else if(text.length() > 2) {
                    String second = StringDifference.difference(text, before);
                    String first = String.valueOf(before.charAt(1));
                    if(Integer.parseInt(second) > 5 && Integer.parseInt(first) > 5) {
                        first = "5";
                        second = "9";
                    } else if (Integer.parseInt(first) > 5) {
                        first = "0";
                    }
                    textView.setText(String.format(Locale.US,("%s%s"), first, second));
                }
            }
        });
        _exerciseTimerSecondText.addTextChangedListener(new TextValidator(_exerciseTimerSecondText) {
            @Override
            public void validate(TextView textView, String text) {
                String before = this.getBefore();
                try{
                    int i = Integer.parseInt(text);
                } catch(Exception e) {
                    textView.setText(before);
                    return;
                }
                if(text.length() < 2) {
                    textView.setText(String.format(Locale.US,("%s%s"), "0", text));
                } else if(text.length() > 2) {
                    String second = StringDifference.difference(text, before);
                    String first = String.valueOf(before.charAt(1));
                    if(Integer.parseInt(second) > 5 && Integer.parseInt(first) > 5) {
                        first = "5";
                        second = "9";
                    } else if (Integer.parseInt(first) > 5) {
                        first = "0";
                    }
                    textView.setText(String.format(Locale.US,("%s%s"), first, second));
                }
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
        ExerciseSet newSet = new ExerciseSet(_exercise.getId(), getNextSetNumber(), 0.0, "lb", 0);
        _adapter.addExerciseSet(newSet);
        updateSets();
        _adapter.notifyDataSetChanged();
        _listView.smoothScrollToPositionFromTop(_adapter.getCount(), 0, 2);

    }

    private void updateSets() {
        for(int i = 0; i <= _listView.getLastVisiblePosition() - _listView.getFirstVisiblePosition(); i++) {
            View item  = _listView.getChildAt(i);
            EditText setWeightET = (EditText) item.findViewById(R.id.view_exercise_set_weight);
            EditText setRepsET = (EditText) item.findViewById(R.id.view_exercise_set_reps);
            try {
                _adapter.getItem(i).setSetWeight(Double.valueOf(setWeightET.getText().toString()));
            } catch (Exception e) {
                _adapter.getItem(i).setSetWeight(0.0);
            }
            try {
                _adapter.getItem(i).setSetReps(Integer.valueOf(setRepsET.getText().toString()));
            } catch (Exception e) {
                _adapter.getItem(i).setSetReps(0);
            }
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
        _exercise.setExerciseName(_exerciseName.getText().toString());
        _exercise.setExerciseSets(_adapter.getExerciseSets());
        _exercise.calculateOneRepMax();
        _db.updateExercise(_exercise);
        _dbe.createExercise(_exerciseName.getText().toString());
    }

    public long getTime(String minutes, String seconds) {
        return (Long.parseLong(minutes) * 60000) + (Long.parseLong(seconds) * 1000);
    }

    @Override
    public void onResume() {
        super.onResume();
        timerInit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();
        if(_exercise != null && _db != null && _dbe != null) {
            updateExercise();
        }

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

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

    public class WorkoutCountDownTimer extends CountDownTimer {
        long timerStartTime;

        public WorkoutCountDownTimer(long startTime, long interval)
        {
            super(startTime, interval);
            timerStartTime = startTime;
        }

        @Override
        public void onTick(long millisUntilFinished) {
            _exerciseTimerMinuteText.setText(String.valueOf((int)Math.floor(millisUntilFinished/1000/60)));
            _exerciseTimerSecondText.setText(String.valueOf((millisUntilFinished/1000) % 60));
        }

        @Override
        public void onFinish() {
            _exerciseTimerMinuteText.setText(R.string.view_exercise_timer_zero);
            _exerciseTimerSecondText.setText(R.string.view_exercise_timer_zero);
            _exerciseTimerButton.setText(R.string.view_exercise_timer_start);
        }
    }

}
