package com.athelite;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
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
    /*
    @Test
    public void setup_fab_test() throws Exception {
        FloatingActionButton fab = (FloatingActionButton) _mockSetupActivity.findViewById(R.id.setup_fab);
        when(fab.performClick()).thenCallRealMethod();

        verify(_mockSetupActivity, atLeastOnce()).validateInformation();
    }
    */
}
