package com.athelite.Dialog;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class ErrorDialog {
    //*********************************************************
    //generic dialog, takes in the method name and error message
    //*********************************************************
    public static void messageBox(String method, String message, Context context)
    {
        Fabric.with(context, new Crashlytics());
        Log.d("EXCEPTION: " + method,  message);
        Toast.makeText(context, method + ": " + message, Toast.LENGTH_SHORT).show();
        Crashlytics.log(1, method, message);
    }

    public static void logError(String method, String message)
    {
        Log.d("EXCEPTION: " + method,  message);
    }
}
