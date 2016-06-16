package com.athelite.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.athelite.BuildConfig;
import com.athelite.Database.DBExerciseList;
import com.athelite.Database.DBHandler;
import com.athelite.Dialog.ErrorDialog;
import com.athelite.R;
import com.athelite.Tabs.CalendarTabFragment;
import com.athelite.Tabs.GraphTabFragment;
import com.athelite.Tabs.HomeTabFragment;
import com.athelite.Tabs.WorkoutPlanTabFragment;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter _sectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager _viewPager;
    private FloatingActionButton _fab;
    private String _currentPage;

    private HomeTabFragment _homeTabFragment;
    private WorkoutPlanTabFragment _workoutPlanTabFragment;
    private CalendarTabFragment _calendarTabFragment;
    private GraphTabFragment _graphTabFragment;

    private final int[] ICONS = new int[]{
            R.drawable.home,
            R.drawable.workout,
            R.drawable.calendar,
            R.drawable.graph
    };

    private static final boolean DEBUG_MODE = "debug".equals(BuildConfig.BUILD_TYPE);

    private void showSetUp(){
        Intent setUpIntent = new Intent(getBaseContext(), SetupActivity.class);
        startActivity(setUpIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean setupSeen = sp.getBoolean("setupSeen", false); //will return false if there is no shared preference
        if(DEBUG_MODE) {
            setupSeen = false;
        }
        if(!setupSeen){
            showSetUp();
            SharedPreferences.Editor ed = sp.edit();
            ed.putBoolean("setupSeen", true);
            ed.apply();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        setFAB();
        setViewPager();
        setupTabIcons();

        DBExerciseList dbe = new DBExerciseList(this);
        try {
            dbe.createDataBase();
        } catch (IOException ioe) {
            Crashlytics.log(1, "ATHELITE_MAIN", "Unable to create database");
            throw new Error("Unable to create database");
        }

        try {
            dbe.openDataBase();
            dbe.close();
        }catch(SQLException sqle){
            Crashlytics.log(1, "ATHELITE_MAIN", sqle.getMessage());
            throw new Error(sqle.getMessage());
        }

        if(DEBUG_MODE) {
            System.out.println("IN DEBUG MODE");
            DBHandler db = new DBHandler(this);
            db.deleteDB();
        }

    }

    @Override
    public void onRestart() {
        super.onRestart();
        try {
            _workoutPlanTabFragment.updateWorkoutPlans();
            _homeTabFragment.updateHomePage();
        } catch (Exception e) {
            ErrorDialog.logError("Error: MainActivity onRestart", e.getMessage());
        }
    }

    public void setFAB() {
        _fab = (FloatingActionButton) findViewById(R.id.view_workout_fab);
        if(_fab != null)
            _fab.hide();
        _fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(_currentPage) {
                    case "Workout":
                        Snackbar.make(view, "Added new Workout", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        _workoutPlanTabFragment.createNewWorkout();
                        break;
                    case "Calendar":
                        Intent intent = new Intent(getBaseContext(), ViewDay.class);
                        intent.putExtra("VIEW_DAY_PARENT", "Calendar");
                        intent.putExtra("VIEW_DAY_DATETIME", _calendarTabFragment.getDateTimeInMilliseconds());
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void setupTabIcons() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        if(tabLayout != null)
            for (int i=0; i < tabLayout.getTabCount(); i++)
            {
                tabLayout.getTabAt(i).setIcon(ICONS[i]);
            }
    }

    public void setViewPager() {
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        _sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        _viewPager = (ViewPager) findViewById(R.id.main_container);
        if (_viewPager != null) {
            _viewPager.setAdapter(_sectionsPagerAdapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(_viewPager);
        }

        _viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        if(getActionBar() != null)
                            getActionBar().setTitle("Home");
                        _currentPage = "Home";
                        _fab.hide();
                        try {
                            HomeTabFragment.getInstance().updateHomePage();
                        } catch (NullPointerException e) {
                            ErrorDialog.logError("Error: Updating Home Page", e.getMessage());
                        }
                        break;
                    case 1:
                        if(getActionBar() != null)
                            getActionBar().setTitle("Workout");
                        _currentPage = "Workout";
                        _fab.setImageResource(android.R.drawable.ic_input_add);
                        _fab.show();
                        try {
                            WorkoutPlanTabFragment.getInstance().updateWorkoutPlans();
                        } catch (NullPointerException e) {
                            ErrorDialog.logError("Error: Updating WorkoutPlans", e.getMessage());
                        }
                        break;
                    case 2:
                        if(getActionBar() != null)
                            getActionBar().setTitle("Calendar");
                        _currentPage = "Calendar";
                        _fab.setImageResource(android.R.drawable.ic_menu_edit);
                        _fab.show();
                        try {
                            CalendarTabFragment.getInstance().updateCalendar();
                        } catch (NullPointerException e) {
                            ErrorDialog.logError("Error: Updating Calendar Page", e.getMessage());
                        }
                        break;
                    case 3:
                        if(getActionBar() != null)
                            getActionBar().setTitle("Graph");
                        _currentPage = "Graph";
                        _fab.hide();
                        try {
                            GraphTabFragment.getInstance().updateExercises();
                        } catch (NullPointerException e) {
                            ErrorDialog.logError("Error: Updating Graph Page", e.getMessage());
                        }
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case R.id.action_help:
                Intent helpIntent = new Intent(getBaseContext(), HelpActivity.class);
                startActivity(helpIntent);
                break;
            case R.id.action_about:
                Intent aboutIntent = new Intent(getBaseContext(), AboutActivity.class);
                startActivity(aboutIntent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            _homeTabFragment = HomeTabFragment.newInstance(0);
            _workoutPlanTabFragment = WorkoutPlanTabFragment.newInstance(1);
            _calendarTabFragment = CalendarTabFragment.newInstance(2);
            _graphTabFragment = GraphTabFragment.newInstance(3);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return _homeTabFragment;
                case 1:
                    return _workoutPlanTabFragment;
                case 2:
                    return _calendarTabFragment;
                case 3:
                    return _graphTabFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "HOME";
                case 1:
                    return "TEMPLATES";
                case 2:
                    return "CALENDAR";
                case 3:
                    return "GRAPH";
            }
            return null;
        }
    }
}
