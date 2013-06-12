package com.braisgabin.dhtbalanced.fragments;

import java.util.List;

import org.holoeverywhere.app.ListFragment;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ArrayAdapter;

import com.braisgabin.dhtbalanced.loaders.LogLoader;

public class LogFragment extends ListFragment implements LoaderManager.LoaderCallbacks<List<String>> {

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
	public Loader<List<String>> onCreateLoader(int arg0, Bundle arg1) {
		return new LogLoader(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<List<String>> arg0, List<String> arg1) {
		setListAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, arg1));
	}

	@Override
	public void onLoaderReset(Loader<List<String>> arg0) {
	}
}
