package com.jameswoo.athelite;

import android.content.Context;

import com.jameswoo.athelite.data.workouts.FakeWorkouts;
import com.jameswoo.athelite.data.settings.FakeSettings;

public class Injection {

    public static WorkoutRepository provideWorkoutRepo(){
        return new FakeWorkouts();
    }

    public static SettingsRepository provideSettingsRepo(Context context) {
        return new FakeSettings();
    }
}
