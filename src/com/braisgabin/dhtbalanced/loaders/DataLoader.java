package com.braisgabin.dhtbalanced.loaders;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;

import com.braisgabin.dhtbalanced.App;
import com.braisgabin.dhtbalanced.utils.Loader;
import com.braisgabin.dhtbalanced.utils.MyHashMap;

public class DataLoader extends Loader<List<Entry<String, String>>> {

	public DataLoader(Context context) {
		super(context);
	}

	@Override
	public List<Entry<String, String>> loadInBackground() {
		return new ArrayList<Entry<String, String>>(((App) App.getLastInstance()).getData().entrySet());
	}

	@Override
	protected void onReleaseResources(List<Entry<String, String>> data) {
	}

	@Override
	protected void onRegisterDataObserver() {
		MyHashMap<String, String> data = ((App) App.getLastInstance()).getData();
		data.setObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				onContentChanged();
			}
		});
	}

	@Override
	protected void onUnregisterDataObserver() {
		MyHashMap<String, String> data = ((App) App.getLastInstance()).getData();
		data.setObserver(null);
	}
}
