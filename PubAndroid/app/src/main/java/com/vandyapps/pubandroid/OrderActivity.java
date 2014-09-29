package com.vandyapps.pubandroid;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import static com.vandyapps.pubandroid.OrderResponse.Order;

public class OrderActivity extends Activity {

    private static final String TAG = OrderActivity.class.getName();

    private AtomicBoolean mBound = new AtomicBoolean(false);

    @InjectView(R.id.number_list)
    TextView mTextView;
    @InjectView(R.id.layout_receipt_holder)
    LinearLayout mLinearLayout;
    @InjectView(R.id.edittext_new_order)
    EditText mEditText;
    private List<Order> mOrders;

    private QueryService mService;
    private Messenger mMessenger = new Messenger(new PubHandler(this));

    private static class PubHandler extends Handler {
        /* We have to use a weak reference here to avoid memory leaks.
         This happens when a message is left on the Looper queue that still has a reference
         to our activity.
        */
        WeakReference<OrderActivity> mReference;

        PubHandler(OrderActivity a) {
            mReference = new WeakReference<OrderActivity>(a);
        }

        @Override
        public void handleMessage(Message msg) {
            OrderActivity a = mReference.get();
            if (a == null)
                return;
            switch (msg.what) {
                case Constants.NEW_DATA:
                    a.updateList((List<Order>) msg.obj);
                    break;
                case Constants.STOP_REQUEST:
                    a.finish();
                    break;
                default:
                    Log.e(TAG, "Got a message with unknown type: " + msg.what);
            }
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        /*
        * This is called when the service we bound to is started.
        */
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            QueryService.QueryBinder b = (QueryService.QueryBinder) binder;
            mService = b.getService();
            mBound.set(true);
            mService.setMessenger(mMessenger);

            // Get the orders that the service is watching for. This is our base
            // truth.
            mLinearLayout.removeAllViews();
            for (Integer i : mService.getWatching())
                addReceiptToList(i);

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound.set(false);
            mService = null;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.inject(this);

        // We have to set our custom font here because apparently
        // there's no way to do it in xml
        try{
            Typeface myTypeface = Typeface.createFromAsset(getAssets(), "dotmatrix.tff");
            mTextView.setTypeface(myTypeface);
        }
        catch (Exception e) {} // Just eat exceptions. Not having that font is no big deal.
    }

    // Bind to the service when we're visible.
    @Override
    protected void onResume () {
        super.onResume();
        Intent i = new Intent(this, QueryService.class);
        bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    // Unbind when we're not. We don't want the service querying if the activity isn't visible
    // (but it will if we started the service in the foreground earlier).
    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mServiceConnection);
    }

    private void updateList(List<Order> orders) {
        // Prevent refreshing when there's no new data
        //if (orders.isEmpty() || orders.get(0).getTimeCreated() <= mLastUpdated)
         //   return;
        //Log.d(TAG, "Updating list with " + orders.toString());
        //mLastUpdated = orders.get(0).getTimeCreated();

        mOrders = orders;
        mTextView.setText("");
        for (Order order : orders) {
            int orderNum = order.getOrderNumber();

            mTextView.append(String.valueOf(orderNum) + " ");

            //
        }
    }

    // Called when a user is adding a new order to watch for.
    @OnClick(R.id.button_add_order)
    public void addOrder() {
        // If we're not bound to the service yet, ask the user to wait.
        if (!mBound.get()) {
            Toast.makeText(this, "Starting service. Please try again.", Toast.LENGTH_SHORT);
            return;
        }

        int orderNum;
        try {
            orderNum = Integer.parseInt(mEditText.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(OrderActivity.this, "Please enter a number",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Add whatever's in the textbox to the list of orders we're watching.
        addReceiptToList(orderNum);

        // Tell the service we're looking for another order
        mService.setNotify(orderNum);

        // Start it in the foreground.
        startService(new Intent(this, QueryService.class));

        // Clear the edit text
        mEditText.setText("");

        // Make a toast telling them we'll notify them when it comes up
        Toast.makeText(this, "We'll notify you when your order is ready. Please keep your receipt.",
                Toast.LENGTH_SHORT).show();
    }

    /**
     * Makes a text view that looks like a receipt for that order and attaches it to the
     * LinearLayout
     */
    private void addReceiptToList(int orderNum) {
        // Inflate the receipt view.
        TextView receipt = (TextView) getLayoutInflater().inflate(R.layout.receipt_item, mLinearLayout, false);

        // Set the text.
        receipt.setText(String.valueOf(orderNum));

        // Add the receipt
        mLinearLayout.addView(receipt, 0);
    }

}