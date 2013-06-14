package com.braisgabin.dhtbalanced.thread;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;

import com.braisgabin.dhtbalanced.App;
import com.braisgabin.dhtbalanced.utils.MyArrayList;
import com.braisgabin.dhtbalanced.utils.Util;

public class InsertThread implements Runnable {
	private final String myIp;
	private final int myId;
	private final String key;
	private final String value;

	public InsertThread(String myIp, String key, String value) {
		this.myIp = myIp;
		this.myId = Util.getId(myIp);
		this.key = key;
		this.value = value;
	}

	public void run() {
		try {
			int id = Util.getId(key);
			int id2 = Util.getId2(key);
			FindLoadThread find1 = new FindLoadThread(myIp, id);
			FindLoadThread find2 = new FindLoadThread(myIp, id2);
			Thread find1T = new Thread(find1);
			Thread find2T = new Thread(find2);

			find1T.start();
			find2T.start();
			find1T.join();
			find2T.join();

			String ip;
			if (find1.load == find2.load) {
				if (new Random().nextBoolean()) {
					ip = find1.ip;
				} else {
					ip = find2.ip;
				}
					
			} else if (find1.load > find2.load) {
				ip = find2.ip;
			} else {
				ip = find1.ip;
			}

			MyArrayList<String> fingerTable = ((App) App.getLastInstance()).getFingerTable();

			insert(ip, key, value);
		} catch (Exception e) {
			e.printStackTrace();
			((App) App.getLastInstance()).getLog().add(e.getMessage());
		}
	}

	public void insert(String ip, String key, String value) throws Exception {
		if (ip.equals(myIp)) {
			((App) App.getLastInstance()).getData().put(key, value);
		}

		((App) App.getLastInstance()).getLog().add("> " + ip + ": insert " + key + " " + value);
		final Socket socket = new Socket(InetAddress.getByName(ip), 7777);
		final PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		out.println("insert " + key + " " + value);
		out.close();
		socket.close();
	}
}
