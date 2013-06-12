package com.braisgabin.dhtbalanced.loaders;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;

import com.braisgabin.dhtbalanced.App;
import com.braisgabin.dhtbalanced.utils.Loader;
import com.braisgabin.dhtbalanced.utils.MyArrayList;

public class FingerTableLoader extends Loader<List<String>> {

	public FingerTableLoader(Context context) {
		super(context);
	}

	@Override
	public List<String> loadInBackground() {
		return ((App) App.getLastInstance()).getFingerTable();
	}

	@Override
	protected void onReleaseResources(List<String> data) {
	}

	@Override
	protected void onRegisterDataObserver() {
		MyArrayList<String> fingerTable = ((App) App.getLastInstance()).getFingerTable();
		fingerTable.setObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				onContentChanged();
			}
		});
	}

	@Override
	protected void onUnregisterDataObserver() {
		MyArrayList<String> fingerTable = ((App) App.getLastInstance()).getFingerTable();
		fingerTable.setObserver(null);
	}
}
