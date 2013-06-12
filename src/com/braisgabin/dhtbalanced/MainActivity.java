package com.braisgabin.dhtbalanced;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;
import org.holoeverywhere.app.Activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TabHost;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.braisgabin.dhtbalanced.fragments.FingerFragment;
import com.braisgabin.dhtbalanced.thread.ClientThread;
import com.braisgabin.dhtbalanced.utils.TabManager;

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
		mTabManager.addTab(mTabHost.newTabSpec("all").setIndicator("Log"), FingerFragment.class, null);
		mTabManager.addTab(mTabHost.newTabSpec("account").setIndicator("Data"), FingerFragment.class, null);

		if (savedInstanceState != null) {
			mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
		}

		String ip = getLocalIpAddress();
		getSupportActionBar().setTitle(ip);
		getSupportActionBar().setSubtitle((0x000000ff & ip.hashCode()) + "");
		System.out.println(getLocalIpAddress());

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

		Thread fst = new Thread(new ClientThread("192.168.2.134"));
		fst.start();

		return true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}

	private String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String sAddr = inetAddress.getHostAddress().toUpperCase();
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (isIPv4)
							return sAddr;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("ServerActivity", ex.toString());
		}
		return null;
	}
}
