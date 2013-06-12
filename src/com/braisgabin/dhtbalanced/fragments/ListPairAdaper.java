package com.braisgabin.dhtbalanced.fragments;

import java.util.List;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

public class ListPairAdaper extends org.holoeverywhere.widget.ArrayAdapter<Pair<String, String>> {

	public ListPairAdaper(Context context, List<Pair<String, String>> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Pair<String, String> item = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
		}
		((TextView) convertView.findViewById(android.R.id.text1)).setText(item.first);
		((TextView) convertView.findViewById(android.R.id.text2)).setText(item.second);
		return convertView;
	}
}
