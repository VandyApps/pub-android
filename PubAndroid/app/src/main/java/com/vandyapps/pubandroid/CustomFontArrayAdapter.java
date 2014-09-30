package com.vandyapps.pubandroid;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Custom String ArrayAdapter that uses a custom font
 */
public class CustomFontArrayAdapter extends ArrayAdapter<String> {
    Context context;
    int layoutResourceId;
    String data[] = null;
    Typeface tf;

    public CustomFontArrayAdapter(Context context, int layoutResourceId, String [] data, String FONT ) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        tf = Typeface.createFromAsset(context.getAssets(), FONT);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView defaultView = (TextView)super.getView(position, convertView, parent);

        defaultView.setTypeface(tf);

        return defaultView;
    }
}
