package com.jameswoo.athelite.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jameswoo.athelite.R;

public class SetupActivity extends AppCompatActivity {

    private SharedPreferences _sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        initToolbar();

        _sp = PreferenceManager.getDefaultSharedPreferences(getApplication());

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

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.setup_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("First Time Setup");
        }
    }

    private void savePreferences() {
        RadioGroup rg = (RadioGroup) findViewById(R.id.units_setup);
        assert rg != null;
        int id = rg.getCheckedRadioButtonId();
        RadioButton r = (RadioButton) findViewById(id);
        assert r != null;

        // Write to shared prefs
        SharedPreferences.Editor editor = _sp.edit();
        if(r.getText().toString().equals("Pounds"))
            editor.putString("units_setup", "lb");
        else if(r.getText().toString().equals("Kilograms"))
            editor.putString("units_setup", "kg");

        editor.apply();
    }

}
