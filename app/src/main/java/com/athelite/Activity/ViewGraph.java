package com.athelite.Activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.athelite.Database.DBHandler;
import com.athelite.Model.Exercise;
import com.athelite.R;
import com.athelite.Util.JsonSerializer;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class ViewGraph extends AppCompatActivity {

    private DBHandler _db;
    private Exercise _exercise;
    private GraphView _graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_graph);

        initToolbar();
        initInstances();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.graph_toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra("VIEW_GRAPH_PARENT"));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void initInstances() {
        _graph = (GraphView) findViewById(R.id.graph_exercise);
        _db = new DBHandler(this);
        _exercise = JsonSerializer.getExerciseFromJson(getIntent().getStringExtra("VIEW_GRAPH_EXERCISE"));
        if(_exercise != null && _graph != null) {
            _graph.setTitle(_exercise.getExerciseName());
            ArrayMap<Date, Double> graphData = _db.getExerciseHistory(_db.getWritableDatabase(), _exercise);
            List<Date> keys = new ArrayList<>(graphData.keySet());
            Collections.sort(keys);
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

            for (int i = 0; i < graphData.size(); i++) {
                DataPoint dp = new DataPoint(keys.get(i), graphData.get(keys.get(i)));
                series.appendData(dp, false, 100);
            }

            _graph.addSeries(series);
            _graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
            _graph.getGridLabelRenderer().setNumHorizontalLabels(3); // only 4 because of the space

            // set manual x bounds to have nice steps
            Calendar c = Calendar.getInstance();
            c.add(Calendar.YEAR, -1);
            if(keys.size() > 0 && keys.get(0).getTime() > c.getTimeInMillis()) {
                _graph.getViewport().setMinX(keys.get(0).getTime());
            } else {
                _graph.getViewport().setMinX(c.getTimeInMillis());
            }
            c.add(Calendar.YEAR, 1);
            _graph.getViewport().setMaxX(c.getTimeInMillis());
            _graph.getViewport().setXAxisBoundsManual(true);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getFragmentManager();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}