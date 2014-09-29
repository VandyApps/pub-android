package com.vandyapps.pubandroid;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);
    }


    @OnClick(R.id.button_orders)
    public void checkOrdersClicked(View v) {
        // Start the order checking activity.
        startActivity(new Intent(this, OrderActivity.class));
    }

    @OnClick(R.id.button_menu)
    public void checkMenuClicked(View v) {
        // Start the order checking activity.
        startActivity(new Intent(this, MenuActivity.class));

    }

}
