package com.braisgabin.dhtbalanced;

import org.holoeverywhere.app.Activity;
import org.holoeverywhere.app.AlertDialog;
import org.holoeverywhere.widget.EditText;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.TabHost;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.braisgabin.dhtbalanced.fragments.DataFragment;
import com.braisgabin.dhtbalanced.fragments.FingerFragment;
import com.braisgabin.dhtbalanced.fragments.LogFragment;
import com.braisgabin.dhtbalanced.thread.ConnectThread;
import com.braisgabin.dhtbalanced.thread.InsertThread;
import com.braisgabin.dhtbalanced.thread.SearchThread;
import com.braisgabin.dhtbalanced.utils.TabManager;
import com.braisgabin.dhtbalanced.utils.Util;

public class MainActivity extends Activity {
	private TabHost mTabHost;
	private TabManager mTabManager;
	private SocketService mBoundService;

	public static Intent getCallingIntent(Context context) {
		Intent intent = new Intent(context, MainActivity.class);

		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();

		mTabManager = new TabManager(this, mTabHost, R.id.realtabcontent);

		mTabManager.addTab(mTabHost.newTabSpec("map").setIndicator("Finger table"), FingerFragment.class, null);
		mTabManager.addTab(mTabHost.newTabSpec("all").setIndicator("Log"), LogFragment.class, null);
		mTabManager.addTab(mTabHost.newTabSpec("account").setIndicator("Data"), DataFragment.class, null);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

		String ip = Util.getLocalIpAddress();
		getSupportActionBar().setTitle(ip);
		getSupportActionBar().setSubtitle(Util.getId(ip) + "");

		((App) App.getLastInstance()).fillFinguerTable(ip);

		bindService(new Intent(this, SocketService.class), mConnection, Context.BIND_AUTO_CREATE);
	}

	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder service) {
			mBoundService = ((SocketService.LocalBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			mBoundService = null;
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		unbindService(mConnection);
		mBoundService = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_join:
				join();
			break;
			case R.id.action_add:
				addItem();
			break;
			case R.id.action_search:
				searchItem();
			break;
		}
		return true;
	}

	private void join() {
		final EditText input = new EditText(this);

		new AlertDialog.Builder(this).setTitle("Add IP")
				.setView(input)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Thread fst = new Thread(new ConnectThread(Util.getLocalIpAddress(), input.getText().toString()));
						fst.start();
					}
				})
				.show();
	}

	private void addItem() {
		final EditText input = new EditText(this);

		new AlertDialog.Builder(this).setTitle("Add Key")
				.setView(input)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						final EditText input2 = new EditText(MainActivity.this);

						new AlertDialog.Builder(MainActivity.this).setTitle("Add Value")
								.setView(input2)
								.setPositiveButton(android.R.string.ok, new OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog, int which) {
										Thread fst = new Thread(new InsertThread(Util.getLocalIpAddress(), input.getText().toString(), input2.getText().toString()));
										fst.start();
									}
								})
								.show();
					}
				})
				.show();
	}

	private void searchItem() {
		final EditText input = new EditText(this);

		new AlertDialog.Builder(this).setTitle("Search item")
				.setView(input)
				.setPositiveButton(android.R.string.ok, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Thread fst = new Thread(new SearchThread(Util.getLocalIpAddress(), input.getText().toString(), Util.getId(input.getText().toString())));
						fst.start();
						Thread fst2 = new Thread(new SearchThread(Util.getLocalIpAddress(), input.getText().toString(), Util.getId2(input.getText().toString())));
						fst2.start();
					}
				})
				.show();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}
}
