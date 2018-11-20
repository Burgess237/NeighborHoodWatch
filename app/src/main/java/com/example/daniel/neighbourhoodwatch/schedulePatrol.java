package com.example.daniel.neighbourhoodwatch;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.borax12.materialdaterangepicker.time.RadialPickerLayout;
import com.borax12.materialdaterangepicker.time.TimePickerDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;

public class schedulePatrol extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,TimePickerDialog.OnTimeSetListener{

    FirebaseAuth mAuth;
    String hourString,minuteString,hourStringEnd,minuteStringEnd;
    String date;

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
            date = year+ "-" + monthOfYear+ "-" + dayOfMonth;
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
        hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
        minuteString = minute < 10 ? "0"+minute : ""+minute;
        hourStringEnd = hourOfDayEnd < 10 ? "0"+hourOfDayEnd : ""+hourOfDayEnd;
        minuteStringEnd = minuteEnd < 10 ? "0"+minuteEnd : ""+minuteEnd;
       saveToDatabase();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_patrol);


        Calendar now = Calendar.getInstance();


        DatePickerDialog dpd = DatePickerDialog.newInstance(
                schedulePatrol.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.show(getFragmentManager(),"Date Picker");

        TimePickerDialog tpd = TimePickerDialog.newInstance(
                this,
                12,
                0,
                true
        );

        tpd.show(getFragmentManager(),"Time Picker");
    }

    public void saveToDatabase(){
        FirebaseDatabase database =  FirebaseDatabase.getInstance();
        FirebaseUser user =  mAuth.getCurrentUser();
        String userId = user.getUid();
        ScheduleResponse sr = new ScheduleResponse(user.getDisplayName(),date,hourString,hourStringEnd);
        DatabaseReference dr = database.getReference(userId).child("schedules").child(date);
        dr.child("date").setValue(sr.getDate());
        dr.child("startHour").setValue(hourString);
        dr.child("endHour").setValue(hourStringEnd);
    }








}
