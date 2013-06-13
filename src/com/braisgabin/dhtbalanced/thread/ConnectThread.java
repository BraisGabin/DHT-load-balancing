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

public class ConnectThread implements Runnable {
	private final String myIp;
	private final int myId;
	private final String initIp;

	public ConnectThread(String myIp, String remoteIp) {
		this.myIp = myIp;
		this.myId = Util.getId(myIp);
		this.initIp = remoteIp;
	}

	public void run() {
		try {
			MyArrayList<String> fingerTable = ((App) App.getLastInstance()).getFingerTable();
			String nextIp = initIp;
			for (int i = 0; i < 8; i++) {
				String lookup = lookup(Util.finguer(myId, i), nextIp);
				nextIp = lookup;
				fingerTable.set(i, lookup);
			}
		} catch (Exception e) {
			e.printStackTrace();
			((App) App.getLastInstance()).getLog().add(e.getMessage());
		}
	}

	public String lookup(int id, String ip) throws Exception {
		if (ip.equals(myIp)) {
			return myIp;
		}
		final int requestId = Util.getId(ip);
		if (requestId == id) {
			return ip;
		}
		((App) App.getLastInstance()).getLog().add("> " + ip + ": lookup " + id);
		final Socket socket = new Socket(InetAddress.getByName(ip), 7777);
		final PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out.println("lookup " + id);
		final String adress = in.readLine();
		out.close();
		in.close();
		socket.close();

		final int adressId = Util.getId(adress);
		final String value;
		if (adressId == id) {
			value = adress;
		} else if (adressId == myId) {
			if (id < myId) {
				if (requestId > myId || requestId < id) {
					value = myIp;
				} else {
					value = ip;
				}
			} else {
				if (requestId > id || requestId < myId) {
					value = ip;
				} else {
					value = myIp;
				}
			}
		} else if (adressId == requestId) {
			value = ip;
		} else {
			value = lookup(id, adress);
		}
		return value;
	}
}
