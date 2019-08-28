package com.example.dcris.myapplication.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.dcris.myapplication.R;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment {
    @NonNull
    @Override

    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        // Get Current Time
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month= c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        String m = String.valueOf(month);
        String d = String.valueOf(day);
        if(month < 10){

            m = "0" + m;
        }
        if(day < 10){

            d  = "0" + d ;
        }

        EditText duration = new EditText(getActivity());
        duration.setInputType(InputType.TYPE_CLASS_NUMBER);


       DatePickerDialog dialog = new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener)getActivity(), year, month, day);
        //min date 2 days from now
     // dialog.getDatePicker().setMinDate(System.currentTimeMillis()+2*24*60*60*1000);
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(120, 120);

        dialog.addContentView(duration,lp);
        return dialog;
        //return new DatePickerDialog(getActivity(),(DatePickerDialog.OnDateSetListener)getActivity(),year,month,day);
    }
}
