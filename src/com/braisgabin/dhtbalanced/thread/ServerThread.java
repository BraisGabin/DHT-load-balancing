package com.braisgabin.dhtbalanced.thread;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import android.util.Log;

public class ServerThread extends Thread {

	private ServerSocket serverSocket;
	private boolean stop = false;

	public void run() {
		try {
			serverSocket = new ServerSocket(7777);
			while (!stop) {
				// listen for incoming clients
				Socket client = serverSocket.accept();

				try {
					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					String line = null;
					while ((line = in.readLine()) != null && !stop) {
						Log.d("ServerActivity", line);
					}
					in.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void forceStop() {
		stop = true;
	}

}
