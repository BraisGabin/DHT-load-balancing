package com.braisgabin.dhtbalanced;

import org.holoeverywhere.widget.Toast;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.braisgabin.dhtbalanced.thread.ServerThread;
import com.braisgabin.dhtbalanced.utils.Util;

public class SocketService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		return myBinder;
	}

	private final IBinder myBinder = new LocalBinder();
	private ServerThread serverThread;

	public class LocalBinder extends Binder {
		public SocketService getService() {
			return SocketService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		serverThread = new ServerThread(Util.getLocalIpAddress());
		serverThread.start();
	}

	public void IsBoundable() {
		Toast.makeText(this, "I bind like butter", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onDestroy() {
		serverThread.forceStop();
	}
}
