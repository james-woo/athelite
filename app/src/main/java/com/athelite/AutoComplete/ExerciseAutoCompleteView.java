package com.athelite.AutoComplete;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

public class ExerciseAutoCompleteView extends AutoCompleteTextView {

    public ExerciseAutoCompleteView(Context context) {
        super(context);
    }

    public ExerciseAutoCompleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExerciseAutoCompleteView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    // this is how to disable AutoCompleteTextView filter
    @Override
    protected void performFiltering(final CharSequence text, final int keyCode) {
        String filterText = "";
        super.performFiltering(filterText, keyCode);
    }

    /*
     * after a selection we have to capture the new value and append to the existing text
     */
    @Override
    protected void replaceText(final CharSequence text) {
        super.replaceText(text);
    }

}