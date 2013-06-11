package com.braisgabin.dhtbalanced;

import org.holoeverywhere.app.Activity;

import com.braisgabin.dhtbalanced.fragments.FingerFragment;
import com.braisgabin.dhtbalanced.utils.TabManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class MainActivity extends Activity {
	private TabHost mTabHost;
	private TabManager mTabManager;

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
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("tab", mTabHost.getCurrentTabTag());
	}
}
