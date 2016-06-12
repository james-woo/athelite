package com.athelite.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.athelite.Database.DBHandler;
import com.athelite.Model.WorkoutPlan;
import com.athelite.R;
import com.athelite.Tabs.CalendarTabFragment;
import com.athelite.Tabs.WorkoutPlanTabFragment;
import com.athelite.Util.JsonSerializer;

import java.sql.Date;

public class DeleteConfirmation extends DialogFragment{
    private DBHandler _db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dialog_fragment_pick_workout, container, false);
        _db = new DBHandler(getActivity());

        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity(), R.style.AlertDialogCustom)
                .setTitle("Are you sure you want to delete?")
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String command = getArguments().getString("DeleteConfirmation.command");
                                switch(command) {
                                    case "Delete Workout":
                                        WorkoutPlan workout =
                                                JsonSerializer.getWorkoutPlanFromJson(getArguments()
                                                        .getString("DeleteConfirmation.workout"));
                                        _db.deleteWorkoutPlan(workout);
                                        getActivity().onBackPressed();
                                        break;
                                    case "Delete WorkoutDay":
                                        WorkoutPlan workoutDay =
                                                JsonSerializer.getWorkoutPlanFromJson(getArguments()
                                                        .getString("DeleteConfirmation.workout"));
                                        _db.deleteWorkoutDay(workoutDay);
                                        getActivity().onBackPressed();
                                        break;
                                }

                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                onDismiss(dialog);
                            }
                        }
                )
                .create();
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }
}
