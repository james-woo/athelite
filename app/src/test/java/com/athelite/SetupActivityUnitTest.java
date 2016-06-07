package com.athelite;

import android.widget.EditText;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import com.athelite.Activity.SetupActivity;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class SetupActivityUnitTest {

    private SetupActivity _mockSetupActivity = Mockito.mock(SetupActivity.class);
    private SetupActivity _activity;


    @Before
    public void setup()  {
        _activity = Robolectric.buildActivity(SetupActivity.class).create().get();
    }

    @Test
    public void setup_activity_starts_test() throws Exception {
        assertTrue(Robolectric.buildActivity(SetupActivity.class).create().get() != null);
    }

    @Test
    public void setup_weight_text_test() throws Exception {
        String weight = _activity.getResources().getString(R.string.setup_weight_string);
        assertThat(weight, equalTo("Weight"));
    }

    @Test
    public void setup_height_text_test() throws Exception {
        String height = _activity.getResources().getString(R.string.setup_height_string);
        assertThat(height, equalTo("Height"));
    }

    @Test
    public void setup_age_text_test() throws Exception {
        String age = _activity.getResources().getString(R.string.setup_age_text_string);
        assertThat(age, equalTo("Age"));
    }

    @Test
    public void validate_information_valid_test() throws Exception {

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
    public void validate_information_invalid_test() throws Exception {

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

}
