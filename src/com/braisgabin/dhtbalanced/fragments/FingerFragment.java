package com.braisgabin.dhtbalanced.fragments;

import java.util.List;

import org.holoeverywhere.app.ListFragment;

import com.braisgabin.dhtbalanced.loaders.FingerTableLoader;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.View;

public class FingerFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<Pair<String, String>>> {

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
	public Loader<List<Pair<String, String>>> onCreateLoader(int arg0, Bundle arg1) {
		return new FingerTableLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<Pair<String, String>>> arg0, List<Pair<String, String>> arg1) {
		setListAdapter(new ListPairAdaper(getActivity(), arg1));
	}

	@Override
	public void onLoaderReset(Loader<List<Pair<String, String>>> arg0) {
	}
}
