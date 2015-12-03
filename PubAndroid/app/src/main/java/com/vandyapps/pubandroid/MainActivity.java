package com.vandyapps.pubandroid;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class MainActivity extends Activity {

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

    @OnClick(R.id.button_hours)
    public void checkHoursClicked(View v) {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.food_description);
        dialog.setTitle("Pub Hours");

        TextView text = (TextView) dialog.findViewById(R.id.tvDescription);
        text.setText("Sun - Thu:\t11:00am - 9:00pm\n" +
                "Fri:\t11:00am - 8:00pm\n" +
                "Sat:\tClosed");
        dialog.show();
    }

}
