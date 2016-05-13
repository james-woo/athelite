package com.jameswoo.athelite.Activity;

import android.content.Intent;
import android.database.SQLException;
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

import com.jameswoo.athelite.Database.DBExerciseList;
import com.jameswoo.athelite.Database.DBHandler;
import com.jameswoo.athelite.R;
import com.jameswoo.athelite.Tabs.CalendarTabFragment;
import com.jameswoo.athelite.Tabs.HomeTabFragment;
import com.jameswoo.athelite.Tabs.WorkoutPlanTabFragment;

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

    private DBHandler _db;

    private final int[] ICONS = new int[]{
            R.drawable.home,
            R.drawable.workout,
            R.drawable.calendar
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HomeTabFragment.getInstance();
        WorkoutPlanTabFragment.getInstance();
        CalendarTabFragment.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setFAB();
        setViewPager();
        setupTabIcons();

        _db = new DBHandler(this);
        DBExerciseList dbe = new DBExerciseList(this);
        try {
            dbe.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }

        try {
            dbe.openDataBase();
        }catch(SQLException sqle){
            throw new Error(sqle.getMessage());
        }

        _db.deleteDB();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        _workoutPlanTabFragment.updateWorkoutPlanAdapter();
        _homeTabFragment.updateHomePage();
    }

    public void setFAB() {
        _fab = (FloatingActionButton) findViewById(R.id.fab);
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
                        intent.putExtra("DATETIME", _calendarTabFragment.getDateTimeInMilliseconds());
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void setupTabIcons() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
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
        _viewPager = (ViewPager) findViewById(R.id.container);
        if (_viewPager != null) {
            _viewPager.setAdapter(_sectionsPagerAdapter);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
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
                        break;
                    case 1:
                        if(getActionBar() != null)
                            getActionBar().setTitle("Workout");
                        _currentPage = "Workout";
                        _fab.setImageResource(android.R.drawable.ic_input_add);
                        _fab.show();
                        break;
                    case 2:
                        if(getActionBar() != null)
                            getActionBar().setTitle("Calendar");
                        _currentPage = "Calendar";
                        _fab.setImageResource(android.R.drawable.ic_menu_edit);
                        _fab.show();
                        break;
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
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    _homeTabFragment = HomeTabFragment.newInstance(0);
                    return _homeTabFragment;
                case 1:
                    _workoutPlanTabFragment = WorkoutPlanTabFragment.newInstance(1);
                    return _workoutPlanTabFragment;
                case 2:
                    _calendarTabFragment = CalendarTabFragment.newInstance(2);
                    return _calendarTabFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
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
            }
            return null;
        }
    }
}
