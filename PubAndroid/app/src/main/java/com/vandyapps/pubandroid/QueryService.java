package com.vandyapps.pubandroid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import retrofit.RestAdapter;

import static android.app.PendingIntent.getActivity;
import static com.vandyapps.pubandroid.OrderResponse.Order;

public class QueryService extends Service {

	private static final String TAG = QueryService.class.getName();

    // Used to schedule regular updates from the server.
	private ScheduledExecutorService mSx = Executors
			.newSingleThreadScheduledExecutor();
	private IBinder mBinder = new QueryBinder();

    // Is the activity bound to us?
    private AtomicBoolean mBound = new AtomicBoolean(false);

    // Is there a specific order we're looking for?
	private AtomicBoolean mNotify = new AtomicBoolean(false);
	private AtomicInteger mOrderNum = new AtomicInteger();

    // Used to send messages back to the Activity.
	private Messenger mMessenger;

    // Configure the REST adapter.
    private PubService pubService= new RestAdapter.Builder()
            .setEndpoint(Constants.SERVER_ADDRESS)
            .build().create(PubService.class);

    private Runnable mQueryRunnable = new Runnable() {

		@Override
		public void run() {

            // If we don't have anyone to tell about new data, don't do anything.
            if (mMessenger == null)
				return;

			try {
				Log.d(TAG, "Querying for new data");

                OrderResponse response =
                        pubService.getOrders(Constants.MAX_NUM_ORDERS, Constants.API_KEY);

                if (!response.getStatus().equals("Okay")) {
                    // The server didn't like our request.
                    return;

                    // TODO - turn off the service and throw an error.
                }

                Message m = Message.obtain();
				m.what = Constants.NEW_DATA;
				m.obj = response.getOrders();
				mMessenger.send(m);
				if (mNotify.get()) {
                    for (Order order : response.getOrders()) {
						if (mOrderNum.get() == order.getOrderNumber()) {

                            // Start building the notification.
							NotificationCompat.Builder b = new NotificationCompat.Builder(
									QueryService.this);

                            // Make our notification pretty.
                            b.setSmallIcon(R.drawable.ic_launcher)
									.setTicker("Order ready")
									.setContentTitle("Pub")
									.setContentText("Your order is ready");

                            // Make our notification make noise
                            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            b.setSound(alarmSound);

                            // When they click on the notification, the Activity should open.
                            Intent intent = new Intent(QueryService.this,
                                    PubActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

	@Override
	public IBinder onBind(Intent intent) {
		mBound.set(true);
		return mBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		stopQueries();
		return false;
	}

	public void setMessenger(Messenger m) {
		mMessenger = m;
	}

	public void setNotify(int n) {
		mOrderNum.set(n);
		mNotify.set(true);
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