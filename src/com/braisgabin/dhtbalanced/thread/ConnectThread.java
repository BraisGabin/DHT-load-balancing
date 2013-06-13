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
		String value;

		if (adressId == myId) {
			// Si el otro dice que soy yo... pues no te lo terminas de creer, hay que comprobarlo!
			value = Util.bestSucessor(id, myIp, ip);
		} else if (id > myId) {
			// Este es el caso normal, le pido a un sucesor mayor sin contar con el modulo 2^8
			if (id <= adressId) {
				// me dan una IP superior a la esperada, por lo tanto es el sucesor.
				value = adress;
			} else if (adressId <= myId) {
				// la ip sigue siendo superior pero hay que controlar el modulo 2^8.
				value = adress;
			} else {
				// la ip se encuentra entre la ip del que pregunta y la del que busca, por lo que seguimos preguntando al nuevo.
				value = lookup(id, adress);
			}
		} else {
			// Este es el caso "raro". Pido a uno con id menor que el mio por cuesti—n del modulo 2^8
			if (adressId > myId) {
				// Aœn no he conseguido pasar la frontera del modulo por lo que sigo buscando
				value = lookup(id, adress);
			} else if (adressId < id) {
				// Sigo sin pasarme, sigo buscando
				value = lookup(id, adress);
			} else {
				// estoy en la zona buena, al cielo con ella
				value = adress;
			}
		}

		return value;
	}
}
