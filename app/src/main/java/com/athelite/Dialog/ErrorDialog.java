package com.athelite.Dialog;

import android.util.Log;

public class ErrorDialog {
    //*********************************************************
    //generic dialog, takes in the method name and error message
    //*********************************************************
    public static void logError(String method, String message)
    {
        Log.d("EXCEPTION: " + method,  message);
    }
}
