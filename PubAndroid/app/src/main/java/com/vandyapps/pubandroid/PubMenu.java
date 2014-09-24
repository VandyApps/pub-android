package com.vandyapps.pubandroid;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import butterknife.InjectView;

public class PubMenu extends Activity {


    @InjectView(R.id.entrees)ListView entreeList;
    @InjectView(R.id.sides)ListView sideList;
    private ArrayAdapter<String>foodAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_menu);
        listSetup();

    }

    private void listSetup(){
        //foodAdapter = new ArrayAdapter<String>(PubMenu.this, android.R.layout.simple_expandable_list_item_1, );
    }
}
