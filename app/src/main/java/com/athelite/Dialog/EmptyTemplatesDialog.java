package com.athelite.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.athelite.Activity.ViewWorkout;
import com.athelite.Adapter.WorkoutPlanAdapter;
import com.athelite.Database.DBHandler;
import com.athelite.Model.WorkoutPlan;
import com.athelite.R;
import com.athelite.Util.JsonSerializer;

public class EmptyTemplatesDialog extends DialogFragment {

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
                .setTitle("No templates found, add a template?")
                .setPositiveButton(R.string.alert_dialog_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                WorkoutPlan workoutPlan = _db.createWorkoutPlan();
                                Intent newTemplate = new Intent(getActivity(), ViewWorkout.class);
                                newTemplate.putExtra("VIEW_WORKOUT_PARENT", "Home");
                                newTemplate.putExtra(WorkoutPlanAdapter.WORKOUT_PLAN, JsonSerializer.workoutPlanToJson(workoutPlan));
                                getActivity().startActivity(newTemplate);
                            }
                        }
                )
                .setNegativeButton(R.string.alert_dialog_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                getActivity().onBackPressed();
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
