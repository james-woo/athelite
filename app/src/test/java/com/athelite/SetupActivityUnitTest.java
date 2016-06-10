package com.athelite;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.robolectric.Shadows.shadowOf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowImageView;
import org.robolectric.shadows.ShadowTextView;
import org.robolectric.shadows.ShadowToast;

import com.athelite.Activity.SetupActivity;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class SetupActivityUnitTest {

    private SetupActivity _activity;


    @Before
    public void setup_activity_setup()  {
        _activity = Robolectric.buildActivity(SetupActivity.class).create().get();
    }

    @Test
    public void setup_activity_starts_test() throws Exception {
        assertTrue(Robolectric.buildActivity(SetupActivity.class).create().get() != null);
    }

    @Test
    public void setup_activity_weight_text_test() throws Exception {
        String weight = _activity.getResources().getString(R.string.setup_weight_string);
        assertThat(weight, equalTo("Weight"));
    }

    @Test
    public void setup_activity_height_text_test() throws Exception {
        String height = _activity.getResources().getString(R.string.setup_height_string);
        assertThat(height, equalTo("Height"));
    }

    @Test
    public void setup_activity_age_text_test() throws Exception {
        String age = _activity.getResources().getString(R.string.setup_age_text_string);
        assertThat(age, equalTo("Age"));
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_validate_information_valid_test() throws Exception {
        EditText weight = (EditText)_activity.findViewById(R.id.setup_edit_weight);
        EditText height = (EditText)_activity.findViewById(R.id.setup_edit_height);
        EditText age = (EditText)_activity.findViewById(R.id.setup_edit_age);
        if(weight != null && height != null && age != null) {
            height.setText("100");
            weight.setText("100");
            age.setText("100");
        }

        boolean valid = _activity.validateInformation();

        assertThat(valid, is(true));
    }

    @Test
    public void setup_activity_validate_information_invalid_test() throws Exception {
        EditText weight = (EditText)_activity.findViewById(R.id.setup_edit_weight);
        EditText height = (EditText)_activity.findViewById(R.id.setup_edit_height);
        EditText age = (EditText)_activity.findViewById(R.id.setup_edit_age);
        if(weight != null && height != null && age != null) {
            height.setText("");
            weight.setText("");
            age.setText("");
        }

        boolean valid = _activity.validateInformation();

        assertThat(valid, is(false));
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_validate_information_invalid_height_test() throws Exception {
        EditText weight = (EditText)_activity.findViewById(R.id.setup_edit_weight);
        EditText height = (EditText)_activity.findViewById(R.id.setup_edit_height);
        EditText age = (EditText)_activity.findViewById(R.id.setup_edit_age);
        if(weight != null && height != null && age != null) {
            height.setText("");
            weight.setText("150");
            age.setText("50");
        }

        boolean valid = _activity.validateInformation();

        assertThat(valid, is(false));
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_validate_information_invalid_weight_test() throws Exception {
        EditText weight = (EditText)_activity.findViewById(R.id.setup_edit_weight);
        EditText height = (EditText)_activity.findViewById(R.id.setup_edit_height);
        EditText age = (EditText)_activity.findViewById(R.id.setup_edit_age);
        if(weight != null && height != null && age != null) {
            height.setText("150");
            weight.setText("");
            age.setText("50");
        }

        boolean valid = _activity.validateInformation();

        assertThat(valid, is(false));
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_validate_information_invalid_age_test() throws Exception {
        EditText weight = (EditText)_activity.findViewById(R.id.setup_edit_weight);
        EditText height = (EditText)_activity.findViewById(R.id.setup_edit_height);
        EditText age = (EditText)_activity.findViewById(R.id.setup_edit_age);
        if(weight != null && height != null && age != null) {
            height.setText("150");
            weight.setText("150");
            age.setText("");
        }

        boolean valid = _activity.validateInformation();

        assertThat(valid, is(false));
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_save_preferences_test() throws Exception {
        EditText weight = (EditText)_activity.findViewById(R.id.setup_edit_weight);
        EditText height = (EditText)_activity.findViewById(R.id.setup_edit_height);
        EditText age = (EditText)_activity.findViewById(R.id.setup_edit_age);
        if(weight != null && height != null && age != null) {
            height.setText("100");
            weight.setText("100");
            age.setText("100");
        }
        _activity.savePreferences();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(_activity);

        assertThat(sp.getString("units_setup", "lbs"), is("lbs"));
        assertThat(sp.getString("units", "lbs"), is("lbs"));
        assertThat(sp.getString("user_height", ""), is("100"));
        assertThat(sp.getString("user_weight", ""), is("100"));
        assertThat(sp.getString("user_age", ""), is("100"));
        assertThat(sp.getString("user_gender", ""), is("Male"));

        RadioGroup rg = (RadioGroup) _activity.findViewById(R.id.setup_units);
        if (rg != null) {
            rg.check(R.id.setup_kilograms);
        }
        _activity.savePreferences();
        assertThat(sp.getString("units_setup", "lbs"), is("kg"));
        assertThat(sp.getString("units", "lbs"), is("kg"));
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_change_weight_valid_test() throws Exception {
        EditText weight = (EditText)_activity.findViewById(R.id.setup_edit_weight);
        if (weight != null) {
            weight.setText("150");
            ShadowTextView weightEditText = shadowOf(weight);
            weightEditText.getWatchers().get(0).afterTextChanged(null);
        }
        assertThat(ShadowToast.shownToastCount(), is(0));
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_change_height_valid_test() throws Exception {
        EditText height = (EditText)_activity.findViewById(R.id.setup_edit_height);
        if (height != null) {
            height.setText("150");
            ShadowTextView heightEditText = shadowOf(height);
            heightEditText.getWatchers().get(0).afterTextChanged(null);
        }
        assertThat(ShadowToast.shownToastCount(), is(0));
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_change_age_valid_test() throws Exception {
        EditText age = (EditText)_activity.findViewById(R.id.setup_edit_age);
        if (age != null) {
            age.setText("50");
            ShadowTextView ageEditText = shadowOf(age);
            ageEditText.getWatchers().get(0).afterTextChanged(null);
        }
        assertThat(ShadowToast.shownToastCount(), is(0));
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_change_weight_invalid_test() throws Exception {
        EditText weight = (EditText)_activity.findViewById(R.id.setup_edit_weight);
        if (weight != null) {
            weight.setText("1500");
            ShadowTextView weightEditText = shadowOf(weight);
            weightEditText.getWatchers().get(0).afterTextChanged(null);
        }
        assertThat(ShadowToast.shownToastCount(), is(1));
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_change_height_invalid_test() throws Exception {
        EditText height = (EditText)_activity.findViewById(R.id.setup_edit_height);
        if (height != null) {
            height.setText("1500");
            ShadowTextView heightEditText = shadowOf(height);
            heightEditText.getWatchers().get(0).afterTextChanged(null);
        }
        assertThat(ShadowToast.shownToastCount(), is(1));
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_change_age_invalid_test() throws Exception {
        EditText age = (EditText)_activity.findViewById(R.id.setup_edit_age);
        if (age != null) {
            age.setText("1500");
            ShadowTextView ageEditText = shadowOf(age);
            ageEditText.getWatchers().get(0).afterTextChanged(null);
        }
        assertThat(ShadowToast.shownToastCount(), is(1));
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_change_weight_exception_test() throws Exception {
        EditText weight = (EditText)_activity.findViewById(R.id.setup_edit_weight);
        if (weight != null) {
            weight.setText(" ");
            ShadowTextView weightEditText = shadowOf(weight);
            weightEditText.getWatchers().get(0).afterTextChanged(null);
            assertThat(weight.getText().toString(), is("0"));
        } else {
            fail();
        }

    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_change_height_exception_test() throws Exception {
        EditText height = (EditText)_activity.findViewById(R.id.setup_edit_height);
        if (height != null) {
            height.setText(" ");
            ShadowTextView heightEditText = shadowOf(height);
            heightEditText.getWatchers().get(0).afterTextChanged(null);
            assertThat(height.getText().toString(), is("0"));
        } else {
            fail();
        }

    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_change_age_exception_test() throws Exception {
        EditText age = (EditText)_activity.findViewById(R.id.setup_edit_age);
        if (age != null) {
            age.setText(" ");
            ShadowTextView ageEditText = shadowOf(age);
            ageEditText.getWatchers().get(0).afterTextChanged(null);
            assertThat(age.getText().toString(), is("0"));
        } else {
            fail();
        }
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_valid_information_test() throws Exception {
        FloatingActionButton fab = (FloatingActionButton) _activity.findViewById(R.id.setup_fab);
        EditText weight = (EditText)_activity.findViewById(R.id.setup_edit_weight);
        EditText height = (EditText)_activity.findViewById(R.id.setup_edit_height);
        EditText age = (EditText)_activity.findViewById(R.id.setup_edit_age);
        if(weight != null && height != null && age != null) {
            height.setText("150");
            weight.setText("150");
            age.setText("20");
        }
        if (fab != null) {
            ShadowImageView fabShadow = shadowOf(fab);
            fabShadow.getOnClickListener().onClick(null);
            assertThat(_activity.validateInformation(), is(true));
        } else {
            fail();
        }
    }

    @SuppressLint("SetTextI18n")
    @Test
    public void setup_activity_invalid_information_toast_test() throws Exception {
        FloatingActionButton fab = (FloatingActionButton) _activity.findViewById(R.id.setup_fab);
        EditText weight = (EditText)_activity.findViewById(R.id.setup_edit_weight);
        EditText height = (EditText)_activity.findViewById(R.id.setup_edit_height);
        EditText age = (EditText)_activity.findViewById(R.id.setup_edit_age);
        if(weight != null && height != null && age != null) {
            height.setText("");
            weight.setText("");
            age.setText("");
        }
        if (fab != null) {
            ShadowImageView fabShadow = shadowOf(fab);
            fabShadow.getOnClickListener().onClick(null);
            assertThat(ShadowToast.getTextOfLatestToast(), is("Your information is invalid"));
        } else {
            fail();
        }
    }

}
