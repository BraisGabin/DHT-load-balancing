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

public class FindLoadThread implements Runnable {
	private final String myIp;
	private final int myId;
	private final int id;
	public int load;
	public String ip;

	public FindLoadThread(String myIp, int id) {
		this.myIp = myIp;
		this.myId = Util.getId(myIp);
		this.id = id;
	}

	public void run() {
		try {
			MyArrayList<String> fingerTable = ((App) App.getLastInstance()).getFingerTable();

			String ip = Util.nextStep(fingerTable, myId, id);
			while (true) {
				String lookup = load(id, ip);
				if (lookup.startsWith("+")) {
					this.load = Integer.parseInt(lookup.substring(1));
					this.ip = ip;
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

	public String load(int id, String ip) throws Exception {
		if (ip.equals(myIp)) {
			return "+" + ((App) App.getLastInstance()).getData().size() + "";
		}
		((App) App.getLastInstance()).getLog().add("> " + ip + ": load " + id);
		final Socket socket = new Socket(InetAddress.getByName(ip), 7777);
		final PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		final BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out.println("load " + id);
		final String adress = in.readLine();
		out.close();
		in.close();
		socket.close();

		return adress;
	}
}
