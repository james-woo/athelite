package com.athelite.Activity;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;
import android.widget.Toast;

import com.athelite.Database.DBHandler;
import com.athelite.Dialog.TimePreference;
import com.athelite.R;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    static SettingsActivity settingsActivity;

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if(preference.getKey().equals("units")) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("switched_units", true);
                editor.apply();
            }
            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else {
                DBHandler db = new DBHandler(preference.getContext());
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
                switch(preference.getKey()) {
                    case "user_name":
                        if(value.toString().equals("")) {
                            Toast.makeText(settingsActivity, "Invalid name", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        break;
                    case "user_height":
                        if(value.toString().equals("")) {
                            Toast.makeText(settingsActivity, "Invalid height", Toast.LENGTH_SHORT).show();
                            return false;
                        } else if(Double.parseDouble(value.toString()) < 0 || Double.parseDouble(value.toString()) > 300 ) {
                            Toast.makeText(settingsActivity, "Invalid height", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if(sp.getString("units", "lb").equals("lb")) {
                            stringValue = value + " cm";
                        } else {
                            stringValue = value + " in";
                        }
                        break;
                    case "user_weight":
                        if(value.toString().equals("")) {
                            Toast.makeText(settingsActivity, "Invalid weight", Toast.LENGTH_SHORT).show();
                            return false;
                        } else if(Double.parseDouble(value.toString()) < 0 || Double.parseDouble(value.toString()) > 1000 ) {
                            Toast.makeText(settingsActivity, "Invalid weight", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        if(sp.getString("units", "lb").equals("lb")) {
                            stringValue = value + " lb";
                        } else {
                            stringValue = value + " kg";
                        }
                        break;
                    case "user_age":
                        if(value.toString().equals("")) {
                            Toast.makeText(settingsActivity, "Invalid age", Toast.LENGTH_SHORT).show();
                            return false;
                        } else if(Double.parseDouble(value.toString()) < 0 || Double.parseDouble(value.toString()) > 150 ) {
                            Toast.makeText(settingsActivity, "Invalid age", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        stringValue = value + " years";
                        break;
                    case "user_bf":
                        if(value.toString().equals("")) {
                            Toast.makeText(settingsActivity, "Invalid body fat percentage", Toast.LENGTH_SHORT).show();
                            return false;
                        } else if(Double.parseDouble(value.toString()) < 0 || Double.parseDouble(value.toString()) > 100 ) {
                            Toast.makeText(settingsActivity, "Invalid body fat percentage", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        stringValue = value + "%";
                        break;
                    case "target_sets":
                        if(value.toString().equals("")) {
                            Toast.makeText(settingsActivity, "Invalid target sets", Toast.LENGTH_SHORT).show();
                            return false;
                        } else if(Double.parseDouble(value.toString()) < 0) {
                            Toast.makeText(settingsActivity, "Invalid target sets", Toast.LENGTH_SHORT).show();
                            return false;
                        } else if(!value.toString().matches("\\d+(?:\\.\\d+)?")) {
                            Toast.makeText(settingsActivity, "Invalid target sets", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        db.updateTargetSets(Integer.parseInt(value.toString()));
                        break;
                    case "target_reps":
                        if(value.toString().equals("")) {
                            Toast.makeText(settingsActivity, "Invalid target reps", Toast.LENGTH_SHORT).show();
                            return false;
                        } else if(Double.parseDouble(value.toString()) < 0) {
                            Toast.makeText(settingsActivity, "Invalid target reps", Toast.LENGTH_SHORT).show();
                            return false;
                        } else if(!value.toString().matches("\\d+(?:\\.\\d+)?")) {
                            Toast.makeText(settingsActivity, "Invalid target sets", Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        db.updateTargetReps(Integer.parseInt(value.toString()));
                        break;
                    case "notification_time":

                        if(value.toString().equals("")) {
                            Toast.makeText(settingsActivity, "Invalid time", Toast.LENGTH_SHORT).show();
                            return false;
                        } else if (TimePreference.getMinute(value.toString()) < 10) {
                            stringValue = String.valueOf(TimePreference.getHour(value.toString())) + ":0" + String.valueOf(TimePreference.getMinute(value.toString()));
                        }
                }
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }

    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        settingsActivity = this;
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || WorkoutPreferenceFragment.class.getName().equals(fragmentName)
                || AdditionalPreferenceFragment.class.getName().equals(fragmentName)
                || AccountPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class WorkoutPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_workout);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            //bindPreferenceSummaryToValue(findPreference("rest_time"));
            //bindPreferenceSummaryToValue(findPreference("timer_alarm"));
            bindPreferenceSummaryToValue(findPreference("units"));
            bindPreferenceSummaryToValue(findPreference("target_sets"));
            bindPreferenceSummaryToValue(findPreference("target_reps"));
            bindPreferenceSummaryToValue(findPreference("notification_time"));

        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_account);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("user_name"));
            //bindPreferenceSummaryToValue(findPreference("email"));
            bindPreferenceSummaryToValue(findPreference("user_height"));
            bindPreferenceSummaryToValue(findPreference("user_weight"));
            bindPreferenceSummaryToValue(findPreference("user_age"));
            bindPreferenceSummaryToValue(findPreference("user_gender"));
            bindPreferenceSummaryToValue(findPreference("user_bf"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AdditionalPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_additional);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            // bindPreferenceSummaryToValue(findPreference("receive_notifications"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
