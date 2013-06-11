package com.braisgabin.dhtbalanced.loaders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.util.Pair;

import com.braisgabin.dhtbalanced.App;
import com.braisgabin.dhtbalanced.utils.Loader;
import com.braisgabin.dhtbalanced.utils.MyHashMap;

public class FingerTableLoader extends Loader<List<Pair<String, String>>> {

	public FingerTableLoader(Context context) {
		super(context);
	}

	@Override
	public List<Pair<String, String>> loadInBackground() {
		HashMap<Integer, String> fingerTable = ((App) App.getLastInstance()).getFingerTable();

		Set<Entry<Integer, String>> entrySet = fingerTable.entrySet();
		List<Entry<Integer, String>> list = new ArrayList<Entry<Integer, String>>(entrySet);
		Collections.sort(list, new Comparator<Entry<Integer, String>>() {

			@Override
			public int compare(Entry<Integer, String> lhs, Entry<Integer, String> rhs) {
				return lhs.getKey().compareTo(rhs.getKey());
			}
		});

		List<Pair<String, String>> l = new ArrayList<Pair<String, String>>();
		for (Entry<Integer, String> e : list) {
			l.add(new Pair<String, String>(e.getKey().toString(), e.getValue()));
		}

		return l;
	}

	@Override
	protected void onReleaseResources(List<Pair<String, String>> data) {
	}

	@Override
	protected void onRegisterDataObserver() {
		MyHashMap<Integer, String> fingerTable = ((App) App.getLastInstance()).getFingerTable();
		fingerTable.setObserver(new Observer() {

			@Override
			public void update(Observable observable, Object data) {
				onContentChanged();
			}
		});
	}

	@Override
	protected void onUnregisterDataObserver() {
		MyHashMap<Integer, String> fingerTable = ((App) App.getLastInstance()).getFingerTable();
		fingerTable.setObserver(null);
	}
}
