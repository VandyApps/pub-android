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
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class QueryService extends Service {

	private static final String TAG = QueryService.class.getName();

	private ScheduledExecutorService mSx = Executors
			.newSingleThreadScheduledExecutor();
	private IBinder mBinder = new QueryBinder();
	private AtomicBoolean mBound = new AtomicBoolean(false);
	private AtomicBoolean mNotify = new AtomicBoolean(false);
	private AtomicInteger mOrderNum = new AtomicInteger();
	private Messenger mMessenger;
	private Runnable mQueryRunnable = new Runnable() {

		@Override
		public void run() {
			if (mMessenger == null)
				return;
			Socket s = null;
			PrintStream ps = null;
			BufferedReader rdr = null;
			try {
				Log.d(TAG, "Querying for new data");
				s = new Socket(Constants.SERVER_IP, Constants.SERVER_PORT);
				ps = new PrintStream(s.getOutputStream());
				rdr = new BufferedReader(new InputStreamReader(
						s.getInputStream()));
				ps.println("read");
				ps.flush();
				String line = rdr.readLine();
				Log.d(TAG, "Got data: " + line);
				Message m = Message.obtain();
				m.what = Constants.NEW_DATA;
				m.obj = line;
				mMessenger.send(m);
				if (mNotify.get()) {
					String orderNum = "" + mOrderNum.get();
					String[] nums = line.split(",");
					for (String str : nums) {
						if (orderNum.equals(str)) {
							Intent intent = new Intent(QueryService.this,
									PubActivity.class);
							NotificationCompat.Builder b = new NotificationCompat.Builder(
									QueryService.this);
							b.setSmallIcon(R.drawable.ic_launcher)
									.setTicker("Order ready")
									.setContentTitle("Pub")
									.setContentText("Your order is ready");
							TaskStackBuilder stackBuilder = TaskStackBuilder
									.create(QueryService.this);
							stackBuilder.addParentStack(PubActivity.class);
							stackBuilder.addNextIntent(intent);
							PendingIntent resultPendingIntent = stackBuilder
									.getPendingIntent(0,
											PendingIntent.FLAG_UPDATE_CURRENT);
							b.setContentIntent(resultPendingIntent);
							NotificationManager nmgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
							nmgr.notify(Constants.ORDER_READY_NOTIFICATION,
									b.build());
						}
					}
				}
			} catch (IOException e) {
				Log.e(TAG, "IOException while querying server", e);
			} catch (RemoteException e) {
				Log.e(TAG, "", e);
			} finally {
				try {
					if (s != null) {
						s.close();
					}
					if (ps != null) {
						ps.close();
					}
					if (rdr != null) {
						rdr.close();
					}
				} catch (IOException e) {
					Log.e(TAG, "Error while closing network IO", e);
				}
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