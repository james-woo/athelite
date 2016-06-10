package com.athelite.Dialog;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ErrorDialog {
    //*********************************************************
    //generic dialog, takes in the method name and error message
    //*********************************************************
    public static void messageBox(String method, String message, Context context)
    {
        Log.d("EXCEPTION: " + method,  message);
        Toast.makeText(context, method + ": " + message, Toast.LENGTH_SHORT).show();
    }
}
