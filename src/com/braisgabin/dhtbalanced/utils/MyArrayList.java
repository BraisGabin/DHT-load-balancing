package com.braisgabin.dhtbalanced.utils;

import java.util.ArrayList;
import java.util.Observer;

public class MyArrayList<T> extends ArrayList<T> {
	private static final long serialVersionUID = 1L;

	private Observer ob;

	public void setObserver(Observer ob) {
		this.ob = ob;
	}

	public boolean add(T object) {
		boolean add = super.add(object);
		if (ob != null) {
			ob.update(null, null);
		}
		return add;
	}

	public T set(int index, T object) {
		T set = super.set(index, object);
		ob.update(null, null);
		return set;
	};
}
