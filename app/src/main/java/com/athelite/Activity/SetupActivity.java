package com.athelite.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.athelite.R;
import com.athelite.Util.TextValidator;

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
        setupEditTextValidation();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.setup_fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateInformation()) {
                    savePreferences();
                    onBackPressed();
                } else {
                    Toast.makeText(SetupActivity.this, "Your information is invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validateInformation() {
        double height, weight, age;
        if(_etHeight.getText().toString().equals("")) {
            return false;
        } else {
            height = Double.parseDouble(_etHeight.getText().toString());
        }
        if(_etWeight.getText().toString().equals("")) {
            return false;
        } else {
            weight = Double.parseDouble(_etWeight.getText().toString());
        }
        if(_etAge.getText().toString().equals("")) {
            return false;
        } else {
            age = Double.parseDouble(_etAge.getText().toString());
        }

        return !((height > 300 || height < 0) || (weight > 1000 || weight < 0) || (age > 150 || age < 0));
    }

    private void initPreferences() {
        _sp = PreferenceManager.getDefaultSharedPreferences(getApplication());

        // Units
        final TextView heightUnitsTV = ((TextView) findViewById(R.id.setup_height_units));
        final TextView weightUnitsTV = ((TextView) findViewById(R.id.setup_weight_units));
        _unitsRadioGroup = (RadioGroup) findViewById(R.id.setup_units);
        _unitsRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton r = (RadioButton) findViewById(checkedId);
                if(heightUnitsTV != null && weightUnitsTV != null) {
                    if (r != null && r.getText().toString().equals("Imperial")) {
                        heightUnitsTV.setText(R.string.setup_inch_tv);
                        weightUnitsTV.setText(R.string.setup_pounds_tv);
                    } else {
                        heightUnitsTV.setText(R.string.setup_cm_tv);
                        weightUnitsTV.setText(R.string.setup_kg_tv);
                    }
                }
            }
        });

        // Gender
        _genderSpinner = (Spinner) findViewById(R.id.setup_gender_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.genderArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _genderSpinner.setAdapter(adapter);

        // Height
        _etHeight = (EditText) findViewById(R.id.setup_edit_height);

        // Weight
        _etWeight = (EditText) findViewById(R.id.setup_edit_weight);

        // Age
        _etAge = (EditText) findViewById(R.id.setup_edit_age);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.setup_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("First Time Setup");
        }
    }

    private void savePreferences() {
        int id = _unitsRadioGroup.getCheckedRadioButtonId();
        RadioButton r = (RadioButton) findViewById(id);
        assert r != null;

        // Write to shared prefs
        SharedPreferences.Editor editor = _sp.edit();
        if(r.getText().toString().equals("Imperial")) {
            editor.putString("units_setup", "lbs");
            editor.putString("units", "lbs");
        }
        else if(r.getText().toString().equals("Metric")) {
            editor.putString("units_setup", "kg");
            editor.putString("units", "kg");
        }

        editor.putString("user_height", _etHeight.getText().toString());
        editor.putString("user_weight", _etWeight.getText().toString());
        editor.putString("user_age", _etAge.getText().toString());
        editor.putString("user_gender", _genderSpinner.getSelectedItem().toString());

        editor.apply();
    }

    private void setupEditTextValidation() {
        _etHeight.addTextChangedListener(new TextValidator(_etHeight) {
            @Override public void validate(TextView textView, String text) {
                if(text.equals("")) {
                    Toast.makeText(SetupActivity.this, "Please enter a valid height", Toast.LENGTH_SHORT).show();
                    return;
                }
                try{
                    if(Double.parseDouble(text) > 300 || Double.parseDouble(text) < 0) {
                        String maxHeight = "300";
                        textView.setText(maxHeight);
                        Toast.makeText(SetupActivity.this, "Please enter a valid height", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Log.e("ATHELITE_SETUP", e.toString());
                    textView.setText("0");
                }
            }
        });
        _etWeight.addTextChangedListener(new TextValidator(_etWeight) {
            @Override public void validate(TextView textView, String text) {
                if(text.equals("")) {
                    Toast.makeText(SetupActivity.this, "Please enter a valid height", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (Double.parseDouble(text) > 1000 || Double.parseDouble(text) < 0) {
                        String maxWeight = "1000";
                        textView.setText(maxWeight);
                        Toast.makeText(SetupActivity.this, "Please enter a valid height", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Log.e("ATHELITE_SETUP", e.toString());
                    textView.setText("0");
                }
            }
        });
        _etAge.addTextChangedListener(new TextValidator(_etAge) {
            @Override public void validate(TextView textView, String text) {
                if(text.equals("")) {
                    Toast.makeText(SetupActivity.this, "Please enter a valid height", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    if (Double.parseDouble(text) > 150 || Double.parseDouble(text) < 0) {
                        String maxAge = "150";
                        textView.setText(maxAge);
                        Toast.makeText(SetupActivity.this, "Please enter a valid height", Toast.LENGTH_SHORT).show();
                    }
                } catch (NumberFormatException e) {
                    Log.e("ATHELITE_SETUP", e.toString());
                    textView.setText("0");
                }
            }
        });
    }

}
