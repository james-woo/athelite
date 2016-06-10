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

import com.athelite.Activity.HelpActivity;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class HelpActivityUnitTest {

    private HelpActivity _activity;

    @Before
    public void help_activity_setup()  {
        _activity = Robolectric.buildActivity(HelpActivity.class).create().get();
    }

    @Test
    public void help_activity_starts_test() throws Exception {
        assertTrue(Robolectric.buildActivity(HelpActivity.class).create().get() != null);
    }

    @Test
    public void help_activity_toolbar_not_null_test() throws Exception {
        Toolbar toolbar = (Toolbar) _activity.findViewById(R.id.help_toolbar);
        assertNotNull(toolbar);
        assertNotNull(_activity.getSupportActionBar());
    }

    @Test
    public void help_activity_toolbar_is_null_test() throws Exception {
        _activity.setSupportActionBar(null);
        assertNull(_activity.getSupportActionBar());

    }
}
