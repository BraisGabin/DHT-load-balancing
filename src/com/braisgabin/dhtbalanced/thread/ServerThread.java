package com.braisgabin.dhtbalanced.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import com.braisgabin.dhtbalanced.App;
import com.braisgabin.dhtbalanced.utils.MyArrayList;
import com.braisgabin.dhtbalanced.utils.Util;

public class ServerThread extends Thread {

	private ServerSocket serverSocket;
	private boolean stop = false;
	private final int myId;
	private final String myIp;

	public ServerThread(String myIp) {
		this.myIp = myIp;
		this.myId = Util.getId(myIp);
	}

	public void run() {
		try {
			serverSocket = new ServerSocket(7777);
			while (!stop) {
				try {
					// listen for incoming clients
					Socket client = serverSocket.accept();

					String remoteIp = client.getInetAddress().getHostAddress();
					refreshFingerTable(remoteIp);
					BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
					String line = null;
					while ((line = in.readLine()) != null && !stop) {
						((App) App.getLastInstance()).getLog().add("< " + client.getInetAddress().getHostAddress() + ": " + line);
						if (line.startsWith("lookup")) {
							String[] split = line.split(" ");
							String adress = getAdress(Integer.parseInt(split[1]));
							out.println(adress);
						}
					}
					in.close();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
					((App) App.getLastInstance()).getLog().add(e.getMessage());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			((App) App.getLastInstance()).getLog().add(e.getMessage());
		}
	}

	private String getAdress(int lookupId) {
		int index = -1;
		int bestId = myId;
		for (int i = 0; i < 8; i++) {
			int id = (myId + (1 << i)) % (1 << 8);
			if (bestId < lookupId) {
				if (lookupId >= id && bestId < id) {
					index = i;
					bestId = id;
				} else {
					break;
				}
			} else {
				if (id >= bestId || id < lookupId) {
					break;
				} else {
					index = i;
					bestId = id;
				}
			}
		}
		return index == -1 ? myIp : ((App) App.getLastInstance()).getFingerTable().get(index);
	}

	private void refreshFingerTable(String remoteIp) {
		MyArrayList<String> fingerTable = ((App) App.getLastInstance()).getFingerTable();
		int remoteId = Util.getId(remoteIp);

		for (int i = 0; i < 8; i++) {
			int id = Util.getId(fingerTable.get(i));
			int desiredId = (myId + (1 << i)) % (1 << 8);
			if (desiredId <= remoteId) {
				if (remoteId <= id || id < desiredId) {
					fingerTable.set(i, remoteIp);
				} else {
					// No hacer nada
				}
			} else {
				if (remoteId < id && id < desiredId) {
					fingerTable.set(i, remoteIp);
				} else {
					// No hacer nada
				}
			}
		}
	}

	public void forceStop() {
		stop = true;
	}

}
