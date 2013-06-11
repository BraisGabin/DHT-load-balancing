package com.braisgabin.dhtbalanced;

import org.holoeverywhere.app.Application;

import com.braisgabin.dhtbalanced.utils.MyHashMap;

public class App extends Application {

	public MyHashMap<Integer, String> getFingerTable() {
		return new MyHashMap<Integer, String>();
	}

}
