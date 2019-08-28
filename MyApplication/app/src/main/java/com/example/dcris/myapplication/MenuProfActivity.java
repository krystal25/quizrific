package com.example.dcris.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.dcris.myapplication.fragments.GrowthFragment;
import com.example.dcris.myapplication.fragments.HomeFragment;
import com.example.dcris.myapplication.fragments.QuizzesFragment;

import java.text.DateFormat;
import java.util.Calendar;

public class MenuProfActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private BottomNavigationView menuNav;
    private FrameLayout menuFrame;
    private HomeFragment homeFragment;
    private QuizzesFragment quizzesFragment;
    private GrowthFragment growthFragment;


    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_prof);


        dl = (DrawerLayout)findViewById(R.id.activity_menu_prof);
        t = new ActionBarDrawerToggle(this, dl,R.string.open, R.string.close);

        dl.addDrawerListener(t);
        t.syncState();

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv_prof);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.account:
                       // Toast.makeText(MenuProfActivity.this, "My Account",Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getApplicationContext(), AccountActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.settings:
                        Toast.makeText(MenuProfActivity.this, "Settings",Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.logout:
                        logOut();
                        return true;
                    default:
                        return true;
                }
            }
        });


        menuFrame = findViewById(R.id.menu_frame);
        menuNav = findViewById(R.id.menu_nav);

        homeFragment = new HomeFragment();
        quizzesFragment = new QuizzesFragment();
        growthFragment = new GrowthFragment();

        setFragment(homeFragment);

        menuNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item){
                    switch(item.getItemId()){
                        case R.id.nav_home:
                            setFragment(homeFragment);
                            return true;
                        case R.id.nav_quizzes:
                            setFragment(quizzesFragment);
                           return true;
                        case R.id.nav_growth:
                            setFragment(growthFragment);
                            return true;
                        default:
                            return false;
                    }
            }
        });



    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }


    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.menu_frame, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR,year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        String currentDate = DateFormat.getDateInstance().format(c.getTime());

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String currentTime ="Hour: " + hourOfDay + " Minute: " + minute;

    }

    private void logOut() {

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        finish();

        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        //finish();
    }

}
