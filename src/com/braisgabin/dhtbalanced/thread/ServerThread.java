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
							String adress = getAdress(Integer.parseInt(split[1]), remoteIp);
							out.println(adress);
						} else if (line.startsWith("find")) {
							// TODO implementar. Buscar si lo tienes tu. Si no lo tienes mirar si eras el responsable (lo eres si tu siguiente no puede serlo)
							// en tal caso devolver -- si no devolver el mejor salto para llegar.
						} else if (line.startsWith("add")) {
							// A–adir
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

	private String getAdress(int lookupId, String remoteIp) {
		int i;
		int remoteId = Util.getId(remoteIp);
		for (i = 0; i < 8; i++) {
			int finguer = Util.finguer(myId, i);
			if (remoteId < myId) {
				if (lookupId > myId) {
					if (finguer > lookupId) {
						break;
					}
				} else {
					if (finguer < myId && finguer > lookupId) {
						break;
					}
				}
			} else {
				if (lookupId > remoteId) {
					break;
				} else if (lookupId <= myId) {
					break;
				} else if (finguer > lookupId) {
					break;
				}
			}
		}
		i--;
		return i == -1 ? myIp : ((App) App.getLastInstance()).getFingerTable().get(i);
	}

	private void refreshFingerTable(String remoteIp) {
		MyArrayList<String> fingerTable = ((App) App.getLastInstance()).getFingerTable();

		for (int i = 0; i < 8; i++) {
			String ip = fingerTable.get(i);
			fingerTable.set(i, Util.bestSucessor(Util.finguer(myId, i), ip, remoteIp));
		}
	}

	public void forceStop() {
		stop = true;
	}

}
