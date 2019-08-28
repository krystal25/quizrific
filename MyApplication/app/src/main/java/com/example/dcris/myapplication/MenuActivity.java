package com.example.dcris.myapplication;

import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.dcris.myapplication.fragments.ChallengeFragment;
import com.example.dcris.myapplication.fragments.HomeFragment;
import com.example.dcris.myapplication.fragments.PracticeFragment;
import com.example.dcris.myapplication.fragments.ProgressFragment;
import com.example.dcris.myapplication.fragments.UpcomingFragment;
import com.pushbots.push.Pushbots;

public class MenuActivity extends AppCompatActivity {

    private BottomNavigationView menuNav;
    private FrameLayout menuFrame;
    private HomeFragment homeFragment;
    private ProgressFragment progressFragment;
    private UpcomingFragment upcomingFragment;
    private PracticeFragment practiceFragment;
    private ChallengeFragment challengeFragment;


    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;

    public static final String SHARED_PREFS = "sharedPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        dl = (DrawerLayout)findViewById(R.id.activity_menu);
        t = new ActionBarDrawerToggle(this, dl,R.string.open, R.string.close);

        dl.addDrawerListener(t);
        t.syncState();

        // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv_student);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.account:
                        Toast.makeText(MenuActivity.this, "My Account",Toast.LENGTH_SHORT).show();
                    case R.id.settings:
                        Toast.makeText(MenuActivity.this, "Settings",Toast.LENGTH_SHORT).show();
                    case R.id.logout:
                        logOut();
                    default:
                        return true;
                }
            }
        });


        menuFrame = findViewById(R.id.menu_frame);
        menuNav = findViewById(R.id.menu_nav);

        homeFragment = new HomeFragment();
        progressFragment = new ProgressFragment();
        upcomingFragment = new UpcomingFragment();
        practiceFragment = new PracticeFragment();
        challengeFragment = new ChallengeFragment();


        setFragment(homeFragment);

        menuNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item){
                    switch(item.getItemId()){
                        case R.id.nav_home:
                           // menuNav.setItemBackgroundResource(R.color.colorPrimary);
                            setFragment(homeFragment);
                            return true;
                        case R.id.nav_progress:
                            //menuNav.setItemBackgroundResource(R.color.colorAccent);
                            setFragment(progressFragment);
                            return true;
                        case R.id.nav_practice:
                            setFragment(practiceFragment);
                            return true;
                        case R.id.nav_upcoming:
                            setFragment(upcomingFragment);
                            return true;
                        case R.id.nav_challenge:
                            setFragment(challengeFragment);
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

    private void logOut() {

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        finish();

        Intent i = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
