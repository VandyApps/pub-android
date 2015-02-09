package com.vandyapps.pubandroid;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.Notification;
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

/**
 * This service queries the Pub Server for information about the orders that are currently waiting.
 * It uses a scheduled executor to query the server every 15 seconds and sends the result back to
 * the activity that is bound to it. The service will optionally wait for certain order numbers,
 * creating a notification when they appear.
 *
 * This service supports two modes: bound and started. When the service is bound, it will continue
 * querying the the server and provide feedback until the activity unbinds.
 *
 * The service may optionally be started(), which will cause it to run in the foreground for an
 * indefinite amount of time. The service will stop itself if it encounters all the orders it was
 * waiting for, or if the user explicitly shuts it down.
 */
public class QueryService extends Service {

    private static final String TAG = QueryService.class.getName();

    // Used to schedule regular updates from the server.
    private ScheduledExecutorService mSx = null;
    private IBinder mBinder = new QueryBinder();

    // Is the activity bound to us?
    private AtomicBoolean mBound = new AtomicBoolean(false);

    // Are there a specific orders we're looking for?
    private Vector<Integer> mWatching = new Vector<Integer>();

    // Used to send messages back to the Activity.
    private Messenger mMessenger;

    // Configure the REST adapter.
    private ServerInterface serverInterface = new RestAdapter.Builder()
            .setEndpoint(Constants.SERVER_ADDRESS)
            .build().create(ServerInterface.class);

    private Runnable mQueryRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                Log.d(TAG, "Querying for new data");

                OrderResponse response =
                        serverInterface.getOrders(Constants.MAX_NUM_ORDERS, Constants.API_KEY);

                if (!response.getStatus().equals("Okay")) {
                    // The server didn't like our request.
                    return;

                    // TODO - turn off the service and throw an error.
                }

                // Only send a message to the activity if we're bound to the activity and have a
                // Messenger.
                if (mBound.get() && mMessenger != null) {
                    Message m = Message.obtain();
                    m.what = Constants.NEW_DATA;
                    m.obj = response.getOrders();
                    mMessenger.send(m);
                }

                // Only make a notification if we're actually waiting for certain orders.
                if (!mWatching.isEmpty()) {
                    for (Order order : response.getOrders()) {
                        if (mWatching.contains(order.getOrderNumber())) {
                            int orderNum = order.getOrderNumber();

                            // Remove the order from the list.
                            removeNotify(orderNum);

                            // Send a notification to tell them their order is ready.
                            NotificationManager nmgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                            nmgr.notify(orderNum, makeOrderReadyNotificaiton(orderNum));
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // This will be triggered by a Pending Intent that is executed
        // when the user clicks on our Foreground Notification. ("Click here to stop")
        if (intent.getAction() == Constants.STOP_ACTION) {
            // Stop the foreground/started portions of the service
            stopForeground(true);
            stopSelf();

            // Tell the activity, if necessary, to finish
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
        // Otherwise, we're being asked to run in the foreground.
        else {
            // Only run in the foreground if there are orders we're waiting on.
            if (!mWatching.isEmpty())
                startForeground(Constants.FOREGROUND_NOTIFICATION, makeForegroundNotification());
            // Otherwise, stop ourselves, just in case
            else
                stopSelf();
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        mBound.set(true);
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "QueryService created.");
        // As long as this service is in memory, make queries.
        startQueries();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "QueryService destroyed.");
        // When everyone unbinds and we're no longer running in the foreground, stop queries.
        stopQueries();
    }

    public void setMessenger(Messenger m) {
        mMessenger = m;
    }

    public void setNotify(int n) {
        // Add the number to the list.
        mWatching.add(n);
    }

    public void removeNotify(int n) {
        //Remvoe the order from the watch list
        mWatching.remove(new Integer(n));

        // If there's no more orders we're waiting for, stop running the started
        // service. (If we're bound, the service will stick around.)
        if (mWatching.isEmpty()) {
            stopForeground(true);
            stopSelf();
        }

    }

    public List<Integer> getWatching() {
        return mWatching;
    }

    private void startQueries() {
        if (mSx == null) {
            mSx = Executors.newSingleThreadScheduledExecutor();
            mSx.scheduleAtFixedRate(mQueryRunnable, 0, Constants.REFRESH_RATE,
                    TimeUnit.SECONDS);
        }
    }

    private void stopQueries() {
        if (mSx != null) {
            mSx.shutdown();
            mSx = null;
        }
    }

    private Notification makeOrderReadyNotificaiton(int orderNum) {
        // Start building the notification.
        NotificationCompat.Builder b = new NotificationCompat.Builder(
                QueryService.this);

        // Make our notification pretty.
        b.setSmallIcon(R.drawable.ic_launcher)
                .setTicker("Order ready")
                .setContentTitle("Pub")
                .setContentText("Order " + orderNum + " is ready");

        // Make our notification make noise
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        b.setSound(alarmSound);

        // When they click on the notification, the Activity should open.
        Intent intent = new Intent(QueryService.this,
                OrderActivity.class);
        b.setContentIntent(getActivity(QueryService.this, 0, intent, 0));

        // When they click on the notification, it should go away.
        b.setAutoCancel(true);

        return b.build();
    }

    private Notification makeForegroundNotification() {
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
        return b.build();
    }
}