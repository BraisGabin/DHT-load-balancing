package com.braisgabin.dhtbalanced.thread;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

import android.util.Log;

public class ClientThread implements Runnable {

	private boolean connected;
	private final String ip;

	public ClientThread(String ip) {
		this.ip = ip;
	}

	public void run() {
		try {
			InetAddress serverAddr = InetAddress.getByName(ip);
			Log.d("ClientActivity", "C: Connecting...");
			Socket socket = new Socket(serverAddr, 7777);
			connected = true;
			if (connected) {
				try {
					Log.d("ClientActivity", "C: Sending command.");
					PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
					// where you issue the commands
					out.println("Hey Server!");
					out.flush();
					Log.d("ClientActivity", "C: Sent.");
				} catch (Exception e) {
					Log.e("ClientActivity", "S: Error", e);
				}
			}
			socket.close();
			Log.d("ClientActivity", "C: Closed.");
		} catch (Exception e) {
			Log.e("ClientActivity", "C: Error", e);
			connected = false;
		}
	}
}
