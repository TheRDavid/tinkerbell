package de.sddb.tinker.test;

import java.io.IOException;
import java.net.UnknownHostException;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletIndustrialQuadRelay;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NetworkException;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;

import de.sddb.tinker.util.Debug;

public class HeartBeat implements Runnable {
	private static final String HOST = "localhost";
	private static final int PORT = 4223;

	private static final String uid = "u1L";
	private BrickletIndustrialQuadRelay relayTail;
	private long offTime;
	private long onTime;

	public HeartBeat() throws UnknownHostException, AlreadyConnectedException, IOException, NetworkException {
		Debug.setDebug(true);
		IPConnection ipcon = new IPConnection(); // Create IP connection
		ipcon.connect(HOST, PORT); // Connect to brickd
		this.relayTail = new BrickletIndustrialQuadRelay(uid, ipcon);
	}

	@Override
	public void run() {
		while (true) {
			try {
				beat();
			} catch (TimeoutException | NotConnectedException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void beat() throws InterruptedException, TimeoutException, NotConnectedException {
		int shortBeat = 50;
		int longBeat = 400;

		set(shortBeat, true);
		set(longBeat, false);
		set(longBeat, true);
		set(longBeat, false);
	}

	private void set(long n, boolean on) throws TimeoutException, NotConnectedException, InterruptedException {
		int val = 0b0000;
		if (on) {
			val = 0b1111;
			onTime+=n;
		} else {
			offTime+=n;
		}
		//Debug.out(onTime+"-"+offTime+"="+(onTime-offTime));
		relayTail.setSelectedValues(0b1111, val);
		Thread.sleep(n);
	}

	public static void main(String[] args) throws Exception {

		HeartBeat br = new HeartBeat();

		// flash(80);
		Thread th = new Thread(br);
		th.start();
	}
}