package com.braisgabin.dhtbalanced.fragments;

import java.util.List;
import java.util.Map.Entry;

import org.holoeverywhere.app.ListFragment;

import com.braisgabin.dhtbalanced.loaders.DataLoader;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;

public class DataFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Entry<String, String>>> {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public Loader<List<Entry<String, String>>> onCreateLoader(int arg0, Bundle arg1) {
		return new DataLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<Entry<String, String>>> arg0, List<Entry<String, String>> arg1) {
		setListAdapter(new ListPairAdaper(getActivity(), arg1));
	}

	@Override
	public void onLoaderReset(Loader<List<Entry<String, String>>> arg0) {
	}
}
