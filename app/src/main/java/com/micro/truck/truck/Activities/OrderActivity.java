package com.micro.truck.truck.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.micro.truck.truck.Fragments.AcceptedFragment;
import com.micro.truck.truck.Fragments.CanceledFragment;
import com.micro.truck.truck.Fragments.CompletedFragment;
import com.micro.truck.truck.Fragments.PendingFragment;
import com.micro.truck.truck.MainActivity;
import com.micro.truck.truck.R;
import com.micro.truck.truck.Utils.GPSTracker;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private BottomNavigationView mBottomNavigation;
    MenuItem pending,on_going,completed,canceled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_order);

        assignUIReferences();
        assignActions();

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(R.string.my_orders);


        PendingFragment chooseServiceFragment = new PendingFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_root_view, chooseServiceFragment)
                .commitAllowingStateLoss();
        mBottomNavigation.getMenu().getItem(0).setChecked(true);


        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("content","pressed");
                onBackPressed();
            }
        });
    }


    protected void assignUIReferences() {
        mToolbar = findViewById(R.id.my_toolbar);
        mBottomNavigation = findViewById(R.id.navigation);
        pending = mBottomNavigation.getMenu().findItem(R.id.pending);
        on_going = mBottomNavigation.getMenu().findItem(R.id.on_going);
        completed = mBottomNavigation.getMenu().findItem(R.id.completed);
        canceled = mBottomNavigation.getMenu().findItem(R.id.canceled);

    }

    protected  void assignActions(){
        mBottomNavigation.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.pending:
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_root_view, new PendingFragment()).commit();
                    menuItem.setIcon(R.drawable.ic_timer_on);
                    completed.setIcon(R.drawable.ic_check_circle_regular);
                    canceled.setIcon(R.drawable.ic_times_circle_regular);
                    return true;
                case R.id.on_going:
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_root_view, new AcceptedFragment()).commit();
//                    menuItem.setIcon(R.drawable.)
                    return true;
                case R.id.completed:
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_root_view, new CompletedFragment()).commit();
                    menuItem.setIcon(R.drawable.ic_check_circle_solid);
                    canceled.setIcon(R.drawable.ic_times_circle_regular);
                    pending.setIcon(R.drawable.ic_timer_off);
                    return true;
                case R.id.canceled:
                    getSupportFragmentManager().beginTransaction().replace(R.id.home_root_view, new CanceledFragment()).commit();
                    menuItem.setIcon(R.drawable.ic_times_circle_solid);
                    pending.setIcon(R.drawable.ic_timer_off);
                    completed.setIcon(R.drawable.ic_check_circle_regular);
                    return true;
            }
            return false;
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent1 = new Intent(getBaseContext() , MainActivity.class);
        intent1.addFlags(intent1.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);
        finish();
    }


}
