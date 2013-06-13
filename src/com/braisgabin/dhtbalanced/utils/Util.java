package com.braisgabin.dhtbalanced.utils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import android.util.Log;

public class Util {

	public static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						String sAddr = inetAddress.getHostAddress().toUpperCase();
						boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						if (isIPv4)
							return sAddr;
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("ServerActivity", ex.toString());
		}
		return null;
	}

	public static int getId(String ip) {
		return (0x000000ff & ip.hashCode());
	}

	public static int log2Floor(int i) {
		return (int) (Math.floor(Math.log(i) / Math.log(2)));
	}

	public static int finguer(int base, int i) {
		return (base + (1 << i)) % (1 << 8);
	}

	public static String bestSucessor(int id, String succesor1, String succesor2) {
		final String value;
		int s1 = Util.getId(succesor1);
		int s2 = Util.getId(succesor2);
		if (s1 > s2) {
			value = bestSucessor(id, succesor2, succesor1);
		} else if (id <= s1) {
			value = succesor1;
		} else if (id <= s2) {
			value = succesor2;
		} else {
			value = succesor1;
		}
		return value;
	}

	public static int getId2(String s) {
		MessageDigest m = null;

		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		m.update(s.getBytes(), 0, s.length());
		return (0x000000ff & new BigInteger(1, m.digest()).intValue());
	}

	public static String nextStep(List<String> finguerTable, int myId, int hash) {
		if (myId > hash) {
			hash += 256;
		}
		int i;
		for (i = 0; i < 8; i++) {
			if (finguer(myId, i) > hash) {
				break;
			}
		}
		return finguerTable.get(i - 1);
	}
}
