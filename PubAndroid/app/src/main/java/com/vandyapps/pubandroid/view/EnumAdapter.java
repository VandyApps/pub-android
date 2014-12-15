package com.vandyapps.pubandroid.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides a general adapter to display a set of Enum in an AdapterView.
 *
 * Created by athran on 12/14/14.
 */
public class EnumAdapter<T extends Enum> extends ArrayAdapter<T> {

    /// Contructors

    public EnumAdapter(Context context, int resource, Class<T> c) {
        super(context, resource, c.getEnumConstants());
    }

    public EnumAdapter(Context context, int resource, int textViewResourceId, Class<T> c) {
        super(context, resource, textViewResourceId, c.getEnumConstants());
    }

    /// Public methods

    public void addDecorator(ViewDecorator d) {
        decorators.add(d);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        for (ViewDecorator d : decorators) {
            d.decorate(v);
        }
        return v;
    }

    private final Set<ViewDecorator> decorators = new HashSet<>();

}
