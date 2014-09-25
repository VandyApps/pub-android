package com.vandyapps.pubandroid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the action bar
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);
    }

    public void checkOrdersClicked(View v) {
        // Start the order checking activity.
        startActivity(new Intent(this, OrderActivity.class));

    }

    public void checkMenuClicked(View v) {
        // Start the order checking activity.
        startActivity(new Intent(this, PubMenu.class));

    }

}
