package com.athelite;

import android.support.v7.widget.Toolbar;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import com.athelite.Activity.AboutActivity;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class AboutActivityUnitTest {

    private AboutActivity _activity;

    @Before
    public void setup()  {
        _activity = Robolectric.buildActivity(AboutActivity.class).create().get();
    }

    @Test
    public void about_activity_starts_test() throws Exception {
        assertTrue(Robolectric.buildActivity(AboutActivity.class).create().get() != null);
    }

    @Test
    public void toolbar_not_null_test() throws Exception {
        Toolbar toolbar = (Toolbar) _activity.findViewById(R.id.about_toolbar);
        assertNotNull(toolbar);
        assertNotNull(_activity.getSupportActionBar());
    }

    @Test
    public void toolbar_is_null_test() throws Exception {
        _activity.setSupportActionBar(null);
        assertNull(_activity.getSupportActionBar());

    }
}
