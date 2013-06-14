package com.braisgabin.dhtbalanced.fragments;

import java.util.List;
import java.util.Map.Entry;

import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.TextView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class ListPairAdaper extends org.holoeverywhere.widget.ArrayAdapter<Entry<String, String>> {

	public ListPairAdaper(Context context, List<Entry<String, String>> objects) {
		super(context, 0, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Entry<String, String> item = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
		}
		((TextView) convertView.findViewById(android.R.id.text1)).setText(item.getKey());
		((TextView) convertView.findViewById(android.R.id.text2)).setText(item.getValue());
		return convertView;
	}
}
