package com.example.myapplication.UI;

import android.content.Context;
import android.widget.ArrayAdapter;

public class EnumSpinnerAdapter<T extends Enum<T>>
        extends ArrayAdapter<T> {

    public EnumSpinnerAdapter(
            Context context,
            T[] values
    ) {
        super(
                context,
                android.R.layout.simple_spinner_item,
                values
        );
        setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
        );
    }
}

