package com.braisgabin.dhtbalanced;

import org.holoeverywhere.app.Application;

import com.braisgabin.dhtbalanced.utils.MyArrayList;
import com.braisgabin.dhtbalanced.utils.MyHashMap;

public class App extends Application {

	private MyArrayList<String> fingerTable;
	private MyArrayList<String> log;
	private MyHashMap<String, String> data;

	public MyArrayList<String> getFingerTable() {
		if (fingerTable == null) {
			fingerTable = new MyArrayList<String>();
		}
		return fingerTable;
	}

	public MyArrayList<String> getLog() {
		if (log == null) {
			log = new MyArrayList<String>();
		}
		return log;
	}

	public MyHashMap<String, String> getData() {
		if (data == null) {
			data = new MyHashMap<String, String>();
		}
		return data;
	}

	public void fillFinguerTable(String ip) {
		MyArrayList<String> fingerTable = getFingerTable();
		fingerTable.clear();
		for (int i = 0; i < 8; i++) {
			fingerTable.add(ip);
		}
	}
}
