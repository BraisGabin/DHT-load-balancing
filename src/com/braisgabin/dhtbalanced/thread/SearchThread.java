package com.braisgabin.dhtbalanced.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import com.braisgabin.dhtbalanced.App;
import com.braisgabin.dhtbalanced.utils.MyArrayList;
import com.braisgabin.dhtbalanced.utils.Util;

public class SearchThread implements Runnable {
	private final String myIp;
	private final int myId;
	private final String key;
	private final int hash;

	public SearchThread(String myIp, String key, int hash) {
		this.myIp = myIp;
		this.myId = Util.getId(myIp);
		this.key = key;
		this.hash = hash;
	}

	public void run() {
		try {
			String string = ((App) App.getLastInstance()).getData().get(key);
			if (string != null) {
				((App) App.getLastInstance()).getLog().add("Finded here dht[" + key + "] = " + string);
				return;
			}

			MyArrayList<String> fingerTable = ((App) App.getLastInstance()).getFingerTable();

			String ip = Util.nextStep(fingerTable, myId, hash);
			while (true) {
				String lookup = find(hash, ip, key);
				if (lookup.startsWith("+")) {
					((App) App.getLastInstance()).getLog().add("Finded at " + ip + " dht[" + key + "] = " + lookup.substring(1));
					break;
				} else if (lookup.startsWith("-")) {
					((App) App.getLastInstance()).getLog().add("No item for dht[" + key + "]");
					break;
				} else {
					ip = lookup;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			((App) App.getLastInstance()).getLog().add(e.getMessage());
		}
	}

	public String find(int id, String ip, String key) throws Exception {
		if (ip.equals(myIp)) {
			return "--";
		}
		((App) App.getLastInstance()).getLog().add("> " + ip + ": find " + id + " " + key);
		final Socket socket = new Socket(InetAddress.getByName(ip), 7777);
		final PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out.println("find " + id + " " + key);
		final String adress = in.readLine();
		out.close();
		in.close();
		socket.close();

		return adress;
	}
}
