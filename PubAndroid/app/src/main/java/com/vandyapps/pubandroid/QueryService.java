package com.vandyapps.pubandroid;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import retrofit.RestAdapter;

import static android.app.PendingIntent.getActivity;
import static android.app.PendingIntent.getService;
import static com.vandyapps.pubandroid.OrderResponse.Order;

public class QueryService extends Service {

    private static final String TAG = QueryService.class.getName();

    // Used to schedule regular updates from the server.
    private ScheduledExecutorService mSx = Executors
            .newSingleThreadScheduledExecutor();
    private IBinder mBinder = new QueryBinder();

    // Is the activity bound to us?
    private AtomicBoolean mBound = new AtomicBoolean(false);

    // Are there a specific orders we're looking for?
    private Vector<Integer> mWatching = new Vector<Integer>();

    // Are we currently running in the foreground?
    private AtomicBoolean mForeground = new AtomicBoolean(false);

    // Used to send messages back to the Activity.
    private Messenger mMessenger;

    // Configure the REST adapter.
    private ServerInterface serverInterface = new RestAdapter.Builder()
            .setEndpoint(Constants.SERVER_ADDRESS)
            .build().create(ServerInterface.class);

    private Runnable mQueryRunnable = new Runnable() {

        @Override
        public void run() {

            // If we don't have anyone to tell about new data, don't do anything.
            if (mMessenger == null)
                return;

            try {
                Log.d(TAG, "Querying for new data");

                OrderResponse response =
                        serverInterface.getOrders(Constants.MAX_NUM_ORDERS, Constants.API_KEY);

                if (!response.getStatus().equals("Okay")) {
                    // The server didn't like our request.
                    return;

                    // TODO - turn off the service and throw an error.
                }

                Message m = Message.obtain();
                m.what = Constants.NEW_DATA;
                m.obj = response.getOrders();
                mMessenger.send(m);
                if (!mWatching.isEmpty()) {
                    for (Order order : response.getOrders()) {
                        if (mWatching.contains(order.getOrderNumber())) {
                            //Remvoe the order from the watch list
                            mWatching.remove(new Integer(order.getOrderNumber()));

                            // If there's no more orders we're waiting for, stop running in
                            // the foreground.
                            if (mWatching.isEmpty() && mForeground.get()) {
                                mForeground.set(false);
                                stopForeground(true);
                            }

                            // Start building the notification.
                            NotificationCompat.Builder b = new NotificationCompat.Builder(
                                    QueryService.this);

                            // Make our notification pretty.
                            b.setSmallIcon(R.drawable.ic_launcher)
                                    .setTicker("Order ready")
                                    .setContentTitle("Pub")
                                    .setContentText("Order " + order.getOrderNumber() + " is ready");

                            // Make our notification make noise
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            b.setSound(alarmSound);

                            // When they click on the notification, the Activity should open.
                            Intent intent = new Intent(QueryService.this,
                                    OrderActivity.class);
                            b.setContentIntent(getActivity(QueryService.this, 0, intent, 0));

                            // When they click on the notification, it should go away.
                            b.setAutoCancel(true);

                            // Send the notification
                            NotificationManager nmgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            nmgr.notify(Constants.ORDER_READY_NOTIFICATION,
                                    b.build());
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception while querying server", e);
            }
        }

    };

    public class QueryBinder extends Binder {
        public QueryService getService() {
            return QueryService.this;
        }
    }

    /**
     * This is only called when the user is asking us to stop running in the foreground.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() == Constants.STOP_ACTION) {
            // Stop running in the foreground
            if (mForeground.get()) {
                mForeground.set(false);
                stopForeground(true);
            }

            // Stop querying the server
            stopQueries();

            // Tell the activity, if we necessary, to finish
            if (mMessenger != null) {
                Message msg = Message.obtain();
                msg.what = Constants.STOP_REQUEST;
                try {
                    mMessenger.send(msg);
                } catch (RemoteException e) {
                    Log.e(TAG, "Couldn't send message from service.");
                }
            }
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBound.set(true);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // Only stop the queries if we're not running in the foreground.
        if (!mForeground.get())
            stopQueries();

        return false;
    }

    public void setMessenger(Messenger m) {
        mMessenger = m;
    }

    public void setNotify(int n) {
        // If this is the first time we're called, start running in the foreground.
        if (mWatching.isEmpty()) {
            // Make users aware that we are constantly querying in the background.

            // Start building the notification.
            NotificationCompat.Builder b = new NotificationCompat.Builder(
                    QueryService.this);

            // Make our notification pretty.
            b.setSmallIcon(R.drawable.ic_launcher)
                    .setTicker("Checking Orders...")
                    .setContentTitle("Checking Orders...")
                    .setContentText("Click here to stop.");

            // When they click on the notification, we should be flagged to stop everything.
            Intent intent = new Intent(QueryService.this,
                    QueryService.class);
            intent.setAction(Constants.STOP_ACTION);
            b.setContentIntent(getService(QueryService.this, 0, intent, 0));

            mForeground.set(true);
            startForeground(1, b.build());
        }

        // Add the number to the list.
        mWatching.add(n);

    }

    public List<Integer> getWatching() {
        return mWatching;
    }

    public void startQueries() {
        if (mSx == null) {
            mSx = Executors.newSingleThreadScheduledExecutor();
        }
        mSx.scheduleAtFixedRate(mQueryRunnable, 0, Constants.REFRESH_RATE,
                TimeUnit.SECONDS);
    }

    public void stopQueries() {
        if (mSx != null) {
            mSx.shutdown();
            mSx = null;
        }
    }

}