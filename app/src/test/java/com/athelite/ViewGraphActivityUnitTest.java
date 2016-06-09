package com.athelite;

import android.content.Intent;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.robolectric.Shadows.shadowOf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.fakes.RoboMenuItem;
import org.robolectric.shadows.ShadowActivity;

import com.athelite.Activity.ViewGraph;
import com.athelite.Model.Exercise;
import com.athelite.Model.ExerciseSet;
import com.athelite.Util.JsonSerializer;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class ViewGraphActivityUnitTest {

    private ViewGraph _activity;
    private Date _date;

    @Before
    public void view_graph_setup()  {
        Calendar c = new GregorianCalendar();
        c.set(Calendar.HOUR_OF_DAY, 0); //anything 0 - 23
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        _date = c.getTime();

        Exercise exercise = new Exercise.Builder("Exercise")
                .exerciseSet(new ExerciseSet(1L, 1, 100.00, "lb", 5))
                .exerciseId(1L)
                .oneRepMax(100.00)
                .exerciseDate(_date)
                .build();

        Intent intent = new Intent();
        intent.putExtra("VIEW_GRAPH_EXERCISE", JsonSerializer.workoutPlanExerciseToJson(exercise));
        intent.putExtra("VIEW_GRAPH_PARENT", "Graph");

        _activity = Robolectric.buildActivity(ViewGraph.class)
                .withIntent(intent)
                .create()
                .get();
    }

    @Test
    public void view_graph_activity_starts_test() throws Exception {
        assertTrue(_activity != null);
    }

    @Test
    public void view_graph_toolbar_not_null_test() throws Exception {
        Toolbar toolbar = (Toolbar) _activity.findViewById(R.id.graph_toolbar);
        assertNotNull(toolbar);
        assertNotNull(_activity.getSupportActionBar());
    }

    @Test
    public void view_graph_toolbar_is_null_test() throws Exception {
        _activity.setSupportActionBar(null);
        assertNull(_activity.getSupportActionBar());

    }

    @Test
    public void view_graph_exercise_is_not_null_test() throws Exception {
        Exercise exercise = JsonSerializer.getExerciseFromJson(_activity.getIntent().getStringExtra("VIEW_GRAPH_EXERCISE"));
        assertTrue(exercise != null);
        GraphView graph = (GraphView) _activity.findViewById(R.id.graph_exercise);
        assertTrue(graph != null);
    }

    @Test
    public void view_graph_add_data_point_to_graph_test() throws Exception {
        ArrayMap<Date, Double> graphData = new ArrayMap<>();
        graphData.put(_date, 100.0);
        long time = _date.getTime();
        graphData.put(new Date(time + time), 200.0);

        List<Date> keys = new ArrayList<>(graphData.keySet());
        Collections.sort(keys);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        for (int i = 0; i < graphData.size(); i++) {
            assertTrue(keys.get(i) != null);
            assertTrue(graphData.get(keys.get(i)) != null);
            DataPoint dp = new DataPoint(keys.get(i), graphData.get(keys.get(i)));
            series.appendData(dp, false, 100);
        }

        assertTrue(graphData.size() > 0);
        assertTrue(!series.isEmpty());
    }

    @Test
    public void view_graph_on_back_pressed_test() throws Exception {
        _activity.onBackPressed();
        ShadowActivity activityShadow = shadowOf(_activity);
        assertTrue(activityShadow.isFinishing());
    }

    @Test
    public void view_graph_on_options_item_home_selected_test() throws Exception {
        MenuItem menuItem = new RoboMenuItem(android.R.id.home);
        _activity.onOptionsItemSelected(menuItem);
        ShadowActivity shadowActivity = Shadows.shadowOf(_activity);
        assertTrue(shadowActivity.isFinishing());
    }

    @Test
    public void view_graph_on_options_item_settings_selected_test() throws Exception {
        MenuItem menuItem = new RoboMenuItem(R.id.action_settings);
        _activity.onOptionsItemSelected(menuItem);
        ShadowActivity shadowActivity = Shadows.shadowOf(_activity);
        assertTrue(shadowActivity.isFinishing());
    }
}
