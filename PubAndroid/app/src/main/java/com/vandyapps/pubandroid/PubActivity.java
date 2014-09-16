package com.vandyapps.pubandroid;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class PubActivity extends Activity {

	private static final String TAG = PubActivity.class.getName();

	private AtomicBoolean mBound = new AtomicBoolean(false);
	private ListView mListView;
	private ArrayAdapter<String> mAdapter;
	private QueryService mService;
	private Messenger mMessenger = new Messenger(new PubHandler(this));
	private String mLastMsg = "";

	private static class PubHandler extends Handler {
		WeakReference<PubActivity> mReference;

		PubHandler(PubActivity a) {
			mReference = new WeakReference<PubActivity>(a);
		}

		@Override
		public void handleMessage(Message msg) {
			PubActivity a = mReference.get();
			if (a == null)
				return;
			switch (msg.what) {
			case Constants.NEW_DATA:
				a.updateList((String) msg.obj);
				break;
			default:
				Log.e(TAG, "Got a message with unknown type: " + msg.what);
			}
		}
	}

	private ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			QueryService.QueryBinder b = (QueryService.QueryBinder) binder;
			mService = b.getService();
			mBound.set(true);
			mService.setMessenger(mMessenger);
			mService.startQueries();
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
		setContentView(R.layout.activity_pub_app);
		mAdapter = new ArrayAdapter<String>(this,
				R.layout.list_item, R.id.list_item_tv);
		mListView = (ListView) findViewById(R.id.number_list);
		mListView.setAdapter(mAdapter);
		Intent i = new Intent(this, QueryService.class);
		bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unbindService(mServiceConnection);
	}

	public void notifyClick(View v) {
		if (!mBound.get())
			return;
		final Dialog dlg = new Dialog(this);
		dlg.setContentView(R.layout.dialog_order_prompt);
		dlg.setTitle(R.string.order_prompt_text);
		final EditText et = (EditText) dlg.findViewById(R.id.prompt_et);
		final Button ok = (Button) dlg.findViewById(R.id.prompt_ok_button);
		final Button cancel = (Button) dlg
				.findViewById(R.id.prompt_cancel_button);
		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String input = et.getText().toString();
				try {
					int orderNum = Integer.parseInt(input);
					mService.setNotify(orderNum);
				} catch (NumberFormatException e) {
					Toast.makeText(PubActivity.this, "Please enter a number",
							Toast.LENGTH_LONG).show();
				}
				dlg.dismiss();
			}

		});
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dlg.dismiss();
			}
		});
		dlg.show();
	}

	private void updateList(String nums) {
		// Prevent refreshing when there's no new data
		if (nums.equals(mLastMsg))
			return;
		Log.d(TAG, "Updating list with " + nums);
		mLastMsg = nums;
		List<String> list = Arrays.asList(nums.split(","));
		Collections.reverse(list);
		mAdapter.clear();
		for (String s : list) {
			mAdapter.add(s);
		}
	}

}