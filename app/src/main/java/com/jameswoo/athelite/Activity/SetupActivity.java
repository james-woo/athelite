package com.jameswoo.athelite.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.jameswoo.athelite.R;

public class SetupActivity extends AppCompatActivity {

    private SharedPreferences _sp;

    private RadioGroup _unitsRadioGroup;
    private Spinner _genderSpinner;
    private EditText _etHeight;
    private EditText _etWeight;
    private EditText _etAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        initToolbar();
        initPreferences();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_done);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePreferences();
                onBackPressed();
            }
        });
    }

    private void initPreferences() {
        _sp = PreferenceManager.getDefaultSharedPreferences(getApplication());

        // Units
        _unitsRadioGroup = (RadioGroup) findViewById(R.id.units_setup);

        // Gender
        _genderSpinner = (Spinner) findViewById(R.id.gender_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genderArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _genderSpinner.setAdapter(adapter);

        // Height
        _etHeight = (EditText) findViewById(R.id.edit_height);

        // Weight
        _etWeight = (EditText) findViewById(R.id.edit_weight);

        // Age
        _etAge = (EditText) findViewById(R.id.edit_age);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.setup_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("First Time Setup");
        }
    }

    private void savePreferences() {

        assert _unitsRadioGroup != null;
        int id = _unitsRadioGroup.getCheckedRadioButtonId();
        RadioButton r = (RadioButton) findViewById(id);
        assert r != null;

        // Write to shared prefs
        SharedPreferences.Editor editor = _sp.edit();
        if(r.getText().toString().equals("Pounds")) {
            editor.putString("units_setup", "lb");
            editor.putString("units", "lb");
        }
        else if(r.getText().toString().equals("Kilograms")) {
            editor.putString("units_setup", "kg");
            editor.putString("units", "kg");
        }

        editor.putString("user_height", _etHeight.getText().toString());
        editor.putString("user_weight", _etWeight.getText().toString());
        editor.putString("user_age", _etAge.getText().toString());
        editor.putString("user_gender", _genderSpinner.getSelectedItem().toString());

        editor.apply();
    }

}
