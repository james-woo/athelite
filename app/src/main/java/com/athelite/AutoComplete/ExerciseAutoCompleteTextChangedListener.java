package com.athelite.AutoComplete;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

public class ExerciseAutoCompleteTextChangedListener implements TextWatcher {

    Context context;

    public ExerciseAutoCompleteTextChangedListener(Context context){
        this.context = context;
    }

    @Override
    public void afterTextChanged(Editable s) {
        // TODO Auto-generated method stub

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTextChanged(CharSequence userInput, int start, int before, int count) {

    }

}