package com.jameswoo.athelite.Activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.Model.Exercise;
import com.jameswoo.athelite.R;
import com.jameswoo.athelite.Util.JsonSerializer;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Date;


public class ViewGraph extends AppCompatActivity {

    private GraphView _graph;
    private TextView _graphExerciseNameTV;
    private DBHandler _db;
    private Exercise _exercise;

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
        _graphExerciseNameTV = (TextView) findViewById(R.id.graph_exercise_name);
        _graph = (GraphView) findViewById(R.id.graph_exercise);

        _exercise = JsonSerializer.getExerciseFromJson(getIntent().getStringExtra("VIEW_GRAPH_EXERCISE"));
        _graphExerciseNameTV.setText(_exercise.getExerciseName());
        _db = new DBHandler(this);
        ArrayMap<Date, Double> graphData = _db.getExerciseHistory(_db.getWritableDatabase(), _exercise);

        ArrayList<DataPoint> data = new ArrayList<>();
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();
        for(int i = 0; i < graphData.size(); i++) {
            DataPoint dp = new DataPoint(graphData.keyAt(i), graphData.valueAt(i));
            series.appendData(dp, false, 100);
        }

        _graph.addSeries(series);
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